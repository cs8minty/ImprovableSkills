package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.api.recipe.RecipeParchmentFragment;

@SimplyRegister
public interface RecipeTypesIS
{
	@RegistryName("parchment_fragment")
	RecipeParchmentFragment.Type PARCHMENT_FRAGMENT_TYPE = new RecipeParchmentFragment.Type();
}