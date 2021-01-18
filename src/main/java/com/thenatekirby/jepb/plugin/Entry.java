package com.thenatekirby.jepb.plugin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("WeakerAccess")
class Entry {
    private final ItemStack itemStack;

    private int min;

    private int max;

    private boolean range = false;

    Entry(@Nonnull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    Entry(@Nonnull IItemProvider item) {
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

    void setNBT(@Nonnull CompoundNBT nbt) {
        this.itemStack.setTag(nbt);
    }

    void setEnchantments(@Nonnull List<Enchantment> enchantments) {
        for (Enchantment enchantment : enchantments) {
            this.itemStack.addEnchantment(enchantment, 1);
        }
    }

    // endregion
}
