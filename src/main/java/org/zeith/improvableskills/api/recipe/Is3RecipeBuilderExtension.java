package org.zeith.improvableskills.api.recipe;

import org.zeith.hammerlib.api.recipes.RecipeBuilderExtension;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

@RecipeBuilderExtension.RegisterExt
public class Is3RecipeBuilderExtension
		extends RecipeBuilderExtension
{
	public Is3RecipeBuilderExtension(RegisterRecipesEvent event)
	{
		super(event);
	}
	
	public ParchmentFragmentBuilder parchment()
	{
		return new ParchmentFragmentBuilder(event);
	}
}