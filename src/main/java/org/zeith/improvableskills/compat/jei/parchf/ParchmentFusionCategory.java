package org.zeith.improvableskills.compat.jei.parchf;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.recipe.RecipeParchmentFragment;
import org.zeith.improvableskills.compat.jei.JeiIS3;
import org.zeith.improvableskills.init.ItemsIS;

import javax.annotation.Nullable;

public class ParchmentFusionCategory
		implements IRecipeCategory<RecipeParchmentFragment>
{
	public IDrawable bg, ic;
	
	public ParchmentFusionCategory(IJeiHelpers helper)
	{
		this.ic = helper.getGuiHelper().createDrawableItemStack(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT));
		this.bg = helper.getGuiHelper().createDrawable(new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/jei.png"), 0, 0, 132, 34);
	}
	
	@Override
	public RecipeType<RecipeParchmentFragment> getRecipeType()
	{
		return JeiIS3.PARCHMENTS;
	}
	
	@Override
	public Component getTitle()
	{
		return Component.translatable("jei." + ImprovableSkills.MOD_ID + ":parchf");
	}
	
	@Override
	public IDrawable getBackground()
	{
		return bg;
	}
	
	@Nullable
	@Override
	public IDrawable getIcon()
	{
		return ic;
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, RecipeParchmentFragment recipe, IFocusGroup focus)
	{
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 8, 9)
				.addItemStack(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT));
		
		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 107, 9)
				.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
		
		int j = 0;
		for(var ci : recipe.ingredients)
		{
			recipeLayout.addSlot(RecipeIngredientRole.INPUT, j * 18 + 26, 9)
					.addIngredients(ci);
			++j;
		}
	}
}
