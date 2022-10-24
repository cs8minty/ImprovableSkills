package org.zeith.improvableskills.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.RecipeParchmentFragment;

@SimplyRegister
public class RecipeTypesIS
{
	@RegistryName("parchment_fragment")
	public static final RecipeParchmentFragment.Serializer PARCHMENT_FRAGMENT_SERIALIZER = new RecipeParchmentFragment.Serializer();
	
	@RegistryName("parchment_fragment")
	public static final RecipeType<RecipeParchmentFragment> PARCHMENT_FRAGMENT_TYPE = RecipeType.simple(new ResourceLocation(ImprovableSkills.MOD_ID, "parchment_fragment"));
}