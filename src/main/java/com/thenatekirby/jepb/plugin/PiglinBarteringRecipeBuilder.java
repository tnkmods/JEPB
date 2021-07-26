package com.thenatekirby.jepb.plugin;

import com.thenatekirby.babel.integration.Mods;
import com.thenatekirby.babel.loot.LootEntryUtil;
import com.thenatekirby.babel.loot.LootFunctionUtil;
import com.thenatekirby.babel.loot.LootTableUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.EnchantRandomly;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// ====---------------------------------------------------------------------------====

@SuppressWarnings("WeakerAccess")
public class PiglinBarteringRecipeBuilder {
    private static LootTableManager manager;

    public static List<PiglinBarteringRecipe> getPiglinBarteringRecipes() {
        LootTable barteringTable = getManager(Minecraft.getInstance().level).get(LootTables.PIGLIN_BARTERING);

        return getLootTableItems(barteringTable)
                .stream()
                .map(PiglinBarteringRecipe::new)
                .collect(Collectors.toList());
    }

    private static LootTableManager getManager(@Nullable World world) {
        if (world != null && world.getServer() != null) {
            return world.getServer().getLootTables();
        }

        if (manager != null) {
            return manager;
        }

        manager = new LootTableManager(new LootPredicateManager());

        List<IResourcePack> packs = new LinkedList<>();
        packs.add(new VanillaPack(Mods.MINECRAFT.getRoot()));
        for (ModFileInfo mod : ModList.get().getModFiles()) {
            packs.add(new ModFileResourcePack(mod.getFile()));
        }

        SimpleReloadableResourceManager serverResourceManger = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
        for (IResourcePack pack : packs) {
            serverResourceManger.add(pack);
        }

        serverResourceManger.registerReloadListener(manager);

        CompletableFuture future = serverResourceManger.reload(
                Util.backgroundExecutor(),
                Minecraft.getInstance(),
                packs,
                CompletableFuture.completedFuture(Unit.INSTANCE));

        Minecraft.getInstance().managedBlock(future::isDone);

        return manager;
    }


    private static List<Entry> getLootTableItems(LootTable table) {
        List<Entry> entries = new ArrayList<>();

        List<LootPool> pools = LootTableUtil.getLootTablePools(table);
        pools.forEach(pool -> {
            List<LootEntry> lootEntries = LootTableUtil.getLootTableEntries(pool);
            lootEntries.stream()
                    .filter(entry -> entry instanceof ItemLootEntry)
                    .forEach(entry -> {
                        Entry lootTableEntry = new Entry(LootEntryUtil.getItem(entry));
                        ILootFunction[] functions = LootEntryUtil.getFunctions(entry);

                        Arrays.stream(functions).forEach(function -> {
                            if (function instanceof SetNBT) {
                                CompoundNBT nbt = LootFunctionUtil.getTag((SetNBT) function);
                                lootTableEntry.setNBT(nbt);

                            } else if (function instanceof EnchantRandomly) {
                                List<Enchantment> enchantments = LootFunctionUtil.getEnchantments((EnchantRandomly) function);
                                lootTableEntry.setEnchantments(enchantments);

                            } else if (function instanceof SetCount) {
                                IRandomRange randomRange = LootFunctionUtil.getCountRange((SetCount) function);

                                if (randomRange.getType() == IRandomRange.UNIFORM) {
                                    RandomValueRange range = (RandomValueRange) randomRange;
                                    lootTableEntry.setRange((int)range.getMin(), (int)range.getMax());
                                }
                            }
                        });

                        entries.add(lootTableEntry);
                    });
        });

        return entries;
    }
}
