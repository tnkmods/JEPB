package com.thenatekirby.jepb.plugin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thenatekirby.jepb.JEPB;
import com.thenatekirby.jepb.Localization;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

// ====---------------------------------------------------------------------------====

public class JEPBRecipeCategory implements IRecipeCategory<PiglinBarteringRecipe> {
    static final ResourceLocation UID = JEPB.MOD.withPath("bartering");

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private IGuiHelper guiHelper;
    private IDrawable background;
    private IDrawable icon;

    private final LoadingCache<PiglinBarteringRecipe, PiglinBarteringRecipeData> cachedData;

    JEPBRecipeCategory(IGuiHelper guiHelper, int width, int height) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(JEPB.MOD.withPath("textures/gui/gui_bartering.png"), 0, 0, width, height);
        this.icon = guiHelper.createDrawableIngredient(getIconItemStack());

        this.cachedData = CacheBuilder.newBuilder()
                .maximumSize(20)
                .build(new CacheLoader<PiglinBarteringRecipe, PiglinBarteringRecipeData>() {
                    @Override
                    public PiglinBarteringRecipeData load(@Nonnull PiglinBarteringRecipe key) {
                        return new PiglinBarteringRecipeData();
                    }
                });
    }

    // ====---------------------------------------------------------------------------====
    // region Helpers

    private ItemStack getIconItemStack() {
        return new ItemStack(Items.GOLD_INGOT);
    }

    private TranslatableComponent getLocalizedName() {
        return Localization.PIGLIN_BARTERING;
    }

    // endregion
    // ====---------------------------------------------------------------------------====
    // region IRecipeCategory

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public Class<? extends PiglinBarteringRecipe> getRecipeClass() {
        return PiglinBarteringRecipe.class;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return getLocalizedName();
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(PiglinBarteringRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, ImmutableList.of(new ItemStack(Items.GOLD_INGOT)));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull PiglinBarteringRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int y = (background.getHeight() / 2) - 10;
        guiItemStacks.init(0, true, 13, y);
        guiItemStacks.init(1, false, 112, y);
        guiItemStacks.set(ingredients);

        PiglinBarteringRecipeData data = cachedData.getUnchecked(recipe);
        data.setRecipe(recipe);
    }

    @Override
    public void draw(@Nonnull PiglinBarteringRecipe recipe, @Nonnull PoseStack matrixStack, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        PiglinBarteringRecipeData data = cachedData.getUnchecked(recipe);
        if (data.displayExtraText()) {
            String extraText = data.getExtraText();

            int textColor = 0xFF888888;
            int width = minecraft.font.width(extraText);
            int x = (121 - (width / 2));
            int y = (background.getHeight() / 2) + 12;
            minecraft.font.draw(matrixStack, extraText, x, y, textColor);
        }
    }

    // endregion
}
