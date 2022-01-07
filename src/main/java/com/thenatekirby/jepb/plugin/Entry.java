package com.thenatekirby.jepb.plugin;

import com.thenatekirby.babel.core.api.IItemProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.List;

// ====---------------------------------------------------------------------------====

@SuppressWarnings("WeakerAccess")
class Entry {
    private ItemStack itemStack;

    private int min;

    private int max;

    private boolean range = false;

    Entry(@Nonnull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    Entry(@Nonnull IItemProvider item) {
        this(new ItemStack(item));
    }

    Entry(@Nonnull ItemLike item) {
        this(new ItemStack(item));
    }

    // ====---------------------------------------------------------------------------====
    // region Getters

    int getMin() {
        return min;
    }

    int getMax() {
        return max;
    }

    ItemStack getItemStack() {
        return itemStack;
    }

    boolean hasRange() {
        return range;
    }

    // endregion
    // ====---------------------------------------------------------------------------====
    // region Setters

    void setRange(int min, int max) {
        this.min = min;
        this.max = max;
        this.range = true;
    }

    void setNBT(@Nonnull CompoundTag nbt) {
        this.itemStack.setTag(nbt);
    }

    void setEnchantments(@Nonnull List<Enchantment> enchantments) {
        if (this.itemStack.getItem() == Items.BOOK) {
            this.itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        for (Enchantment enchantment : enchantments) {
            this.itemStack.enchant(enchantment, 1);
        }
    }

    void setPotion(@Nonnull Potion potion) {
        PotionUtils.setPotion(itemStack, potion);
    }

    // endregion
}
