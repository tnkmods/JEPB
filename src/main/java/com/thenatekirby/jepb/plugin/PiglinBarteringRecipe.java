package com.thenatekirby.jepb.plugin;

import net.minecraft.item.ItemStack;

// ====---------------------------------------------------------------------------====

class PiglinBarteringRecipe {
    private final Entry lootTableEntry;

    PiglinBarteringRecipe(Entry lootTableEntry) {
        this.lootTableEntry = lootTableEntry;
    }

    ItemStack getResult() {
        return lootTableEntry.getItemStack();
    }

    Entry getLootTableEntry() {
        return lootTableEntry;
    }
}
