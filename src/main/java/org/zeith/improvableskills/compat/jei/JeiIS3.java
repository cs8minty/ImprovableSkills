package org.zeith.improvableskills.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.client.gui.abil.anvil.AnvilMenuPortable;
import org.zeith.improvableskills.client.gui.abil.crafting.CraftingMenuPortable;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.GuiHooksIS;
import org.zeith.improvableskills.init.ItemsIS;

import java.util.Optional;

@JeiPlugin
public class JeiIS3
		implements IModPlugin
{
	public static final ResourceLocation JEI = ImprovableSkills.id("jei");
	
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(AnvilMenuPortable.class, GuiHooksIS.REPAIR, RecipeTypes.ANVIL, 0, 2, 3, 36);
		registration.addRecipeTransferHandler(CraftingMenuPortable.class, GuiHooksIS.CRAFTING, RecipeTypes.CRAFTING, 1, 9, 10, 36);
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