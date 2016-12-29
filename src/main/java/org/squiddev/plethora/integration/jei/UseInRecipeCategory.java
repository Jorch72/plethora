package org.squiddev.plethora.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import org.squiddev.plethora.core.PlethoraCore;
import org.squiddev.plethora.utils.Helpers;

import javax.annotation.Nonnull;

public class UseInRecipeCategory extends BlankRecipeCategory<UseInRecipeWrapper> {
	public static final int recipeWidth = 160;
	public static final int recipeHeight = 125;

	private final String id;
	private final IDrawable background;

	public UseInRecipeCategory(String id, IGuiHelper helper) {
		this.id = id;
		background = helper.createBlankDrawable(recipeWidth, recipeHeight);
	}

	@Nonnull
	@Override
	public String getUid() {
		return PlethoraCore.ID + ":" + id;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Helpers.translateToLocal("gui.jei.plethora." + id);
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull UseInRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		int xPos = (recipeWidth - 18) / 2;
		guiItemStacks.init(0, true, xPos, 0);
		guiItemStacks.setFromRecipe(0, recipeWrapper.getInputs());
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull UseInRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		int xPos = (recipeWidth - 18) / 2;
		guiItemStacks.init(0, true, xPos, 0);
		guiItemStacks.set(ingredients);
	}
}
