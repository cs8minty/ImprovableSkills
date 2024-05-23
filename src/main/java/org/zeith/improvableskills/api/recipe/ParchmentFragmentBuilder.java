package org.zeith.improvableskills.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.recipe.RecipeBuilder;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;
import org.zeith.improvableskills.init.RecipeTypesIS;

public class ParchmentFragmentBuilder
		extends RecipeBuilder<ParchmentFragmentBuilder, Recipe<?>>
{
	protected final NonNullList<Ingredient> ingredients = NonNullList.create();
	protected boolean identifierSet;
	
	public ParchmentFragmentBuilder(IRecipeRegistrationEvent<Recipe<?>> event)
	{
		super(event);
	}
	
	public ParchmentFragmentBuilder abilityScroll(PlayerAbilityBase abil)
	{
		if(identifier == null)
		{
			var id = abil.getRegistryName();
			if(id == null) return this;
			identifierSet = true;
			id(new ResourceLocation(id.getNamespace(), "ability_scrolls/" + id.getPath()));
		}
		return result(ItemAbilityScroll.of(abil));
	}
	
	public ParchmentFragmentBuilder add(Object ingredient)
	{
		this.ingredients.add(RecipeHelper.fromComponent(ingredient));
		return this;
	}
	
	public ParchmentFragmentBuilder addAll(Object... ingredients)
	{
		for(Object ingredient : ingredients) this.ingredients.add(RecipeHelper.fromComponent(ingredient));
		return this;
	}
	
	public ParchmentFragmentBuilder addAll(Iterable<Object> ingredients)
	{
		for(Object ingredient : ingredients) this.ingredients.add(RecipeHelper.fromComponent(ingredient));
		return this;
	}
	
	@Override
	public void register()
	{
		if(!identifierSet) return;
		validate();
		if(!event.enableRecipe(RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE, getIdentifier())) return;
		if(ingredients.isEmpty())
			throw new IllegalStateException(getClass().getSimpleName() + " does not have any defined ingredients!");
		var id = getIdentifier();
		event.register(id, new RecipeParchmentFragment(id, group, result, ingredients));
	}
}