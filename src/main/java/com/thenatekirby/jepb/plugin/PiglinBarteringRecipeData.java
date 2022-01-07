package com.thenatekirby.jepb.plugin;

// ====---------------------------------------------------------------------------====

class PiglinBarteringRecipeData {
    private String extraText;

    void setRecipe(PiglinBarteringRecipe recipe) {
        Entry entry = recipe.getLootTableEntry();

        if (entry.hasRange()) {
            extraText = entry.getMin() + "-" + entry.getMax();
        }
    }

    String getExtraText() {
        return extraText;
    }

    boolean displayExtraText() {
        return (extraText != null);
    }
}
