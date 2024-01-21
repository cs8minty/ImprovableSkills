package org.zeith.improvableskills.init;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.improvableskills.api.RecipeParchmentFragment;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;

@ProvideRecipes
public class RecipesIS
		implements IRecipeProvider
{
	@Override
	public void provideRecipes(RegisterRecipesEvent e)
	{
		e.shaped()
				.shape("lbl", "pgp", "lbl")
				.map('l', Tags.Items.LEATHER)
				.map('b', Items.BOOK)
				.map('p', Items.PAPER)
				.map('g', Tags.Items.INGOTS_GOLD)
				.result(ItemsIS.SKILLS_BOOK)
				.register();
		
		e.shapeless()
				.add(ItemsIS.PARCHMENT_FRAGMENT)
				.result(new ItemStack(Items.PAPER, 7))
				.register();
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.ANVIL.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.ANVIL),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ANVIL),
						RecipeHelper.fromTag(Tags.Items.GEMS_EMERALD)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.CRAFTER.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.CRAFTER),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.CRAFTING_TABLE),
						RecipeHelper.fromTag(Tags.Items.INGOTS_IRON)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.ENCHANTING.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.ENCHANTING),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ENCHANTING_TABLE),
						RecipeHelper.fromComponent(Items.BOOKSHELF)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.MAGNETISM.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.MAGNETISM),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ENDER_EYE),
						RecipeHelper.fromComponent(Items.IRON_INGOT),
						RecipeHelper.fromComponent(Items.CHAIN)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.AUTO_XP_BANK.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.AUTO_XP_BANK),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.EXPERIENCE_BOTTLE),
						RecipeHelper.fromComponent(Items.REDSTONE)
				)
		));
	}
}