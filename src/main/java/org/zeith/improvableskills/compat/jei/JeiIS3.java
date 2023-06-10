package org.zeith.improvableskills.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.recipe.RecipeParchmentFragment;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.client.gui.abil.anvil.AnvilMenuPortable;
import org.zeith.improvableskills.client.gui.abil.crafting.CraftingMenuPortable;
import org.zeith.improvableskills.compat.jei.parchf.ParchmentFusionCategory;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.*;

import java.util.Optional;

@JeiPlugin
public class JeiIS3
		implements IModPlugin
{
	public static final ResourceLocation JEI = new ResourceLocation(ImprovableSkills.MOD_ID, "jei");
	
	public static final RecipeType<RecipeParchmentFragment> PARCHMENTS = RecipeType.create(ImprovableSkills.MOD_ID, "parcment_fusion", RecipeParchmentFragment.class);
	
	public static <T> void checkNotNull(@Nullable T object, String name)
	{
		if(object == null)
		{
			throw new NullPointerException(name + " must not be null.");
		}
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		Minecraft minecraft = Minecraft.getInstance();
		checkNotNull(minecraft, "minecraft");
		ClientLevel world = minecraft.level;
		checkNotNull(world, "minecraft world");
		var recipeManager = world.getRecipeManager();
		
		registration.addRecipes(PARCHMENTS, recipeManager.getAllRecipesFor(RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalyst(new ItemStack(ItemsIS.SKILLS_BOOK), PARCHMENTS);
	}
	
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(AnvilMenuPortable.class, GuiHooksIS.REPAIR, RecipeTypes.ANVIL, 0, 2, 3, 36);
		registration.addRecipeTransferHandler(CraftingMenuPortable.class, GuiHooksIS.CRAFTING, RecipeTypes.CRAFTING, 1, 9, 10, 36);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new ParchmentFusionCategory(registry.getJeiHelpers()));
	}
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration)
	{
		registration.registerSubtypeInterpreter(ItemsIS.ABILITY_SCROLL, (itemStack, context) ->
				Optional.ofNullable(ItemAbilityScroll.getAbilityFromScroll(itemStack)).map(PlayerAbilityBase::getRegistryName).map(ResourceLocation::toString).orElse("null")
		);
		
		registration.registerSubtypeInterpreter(ItemsIS.SKILL_SCROLL, (itemStack, context) ->
				Optional.ofNullable(ItemSkillScroll.getSkillFromScroll(itemStack)).map(PlayerSkillBase::getRegistryName).map(ResourceLocation::toString).orElse("null")
		);
	}
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return JEI;
	}
}