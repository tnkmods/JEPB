package com.thenatekirby.jepb.plugin;

import com.thenatekirby.babel.integration.Mods;
import com.thenatekirby.babel.util.loot.LootEntryUtil;
import com.thenatekirby.babel.util.loot.LootFunctionUtil;
import com.thenatekirby.babel.util.loot.LootRange;
import com.thenatekirby.babel.util.loot.LootTableUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;

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
    private static LootTables manager;

    public static List<PiglinBarteringRecipe> getPiglinBarteringRecipes() {
        LootTable barteringTable = getManager(Minecraft.getInstance().level).get(BuiltInLootTables.PIGLIN_BARTERING);

        return getLootTableItems(barteringTable)
                .stream()
                .map(PiglinBarteringRecipe::new)
                .collect(Collectors.toList());
    }

    private static LootTables getManager(@Nullable Level level) {
        if (level != null && level.getServer() != null) {
            return level.getServer().getLootTables();
        }

        if (manager != null) {
            return manager;
        }

        manager = new LootTables(new PredicateManager());

        List<PackResources> packs = new LinkedList<>();
        packs.add(new VanillaPackResources(ServerPacksSource.BUILT_IN_METADATA, Mods.MINECRAFT.getRoot()));

        // TODO: Modstuffs
//        ModList.get().getModFiles()
        for (IModFileInfo mod : ModList.get().getModFiles()) {
//            packs.add(new ModFileResourcePack(mod.getFile()));
        }

        SimpleReloadableResourceManager serverResourceManger = new SimpleReloadableResourceManager(PackType.SERVER_DATA);
        for (PackResources pack : packs) {
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
            List<LootPoolEntryContainer> lootEntries = Arrays.asList(LootTableUtil.getLootTableEntries(pool));
            lootEntries.stream()
                    .filter(entry -> entry instanceof LootItem)
                    .forEach(entry -> {
                        Entry lootTableEntry = new Entry(LootEntryUtil.getItem(entry));
                        LootItemFunction[] functions = LootEntryUtil.getFunctions(entry);

                        Arrays.stream(functions).forEach(function -> {
                            if (function instanceof SetNbtFunction) {
                                CompoundTag nbt = LootFunctionUtil.getTag((SetNbtFunction) function);
                                lootTableEntry.setNBT(nbt);

                            } else if (function instanceof EnchantRandomlyFunction) {
                                List<Enchantment> enchantments = LootFunctionUtil.getEnchantments((EnchantRandomlyFunction) function);
                                lootTableEntry.setEnchantments(enchantments);

                            } else if (function instanceof SetItemCountFunction) {
                                NumberProvider randomRange = LootFunctionUtil.getValue((SetItemCountFunction) function);

                                if (randomRange.getType() == NumberProviders.UNIFORM) {
                                    UniformGenerator uniformGenerator = (UniformGenerator) randomRange;
                                    LootRange range = LootRange.from(uniformGenerator);
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
