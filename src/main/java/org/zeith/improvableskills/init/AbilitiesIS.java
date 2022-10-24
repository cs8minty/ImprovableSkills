package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.custom.abilities.*;

@SimplyRegister
public class AbilitiesIS
{
	@RegistryName("enchanting")
	public static final AbilityEnchanting ENCHANTING = new AbilityEnchanting();
	
	@RegistryName("crafter")
	public static final AbilityCrafter CRAFTER = new AbilityCrafter();
	
	@RegistryName("anvil")
	public static final AbilityAnvil ANVIL = new AbilityAnvil();
	
	@RegistryName("magnetism")
	public static final AbilityMagnetism MAGNETISM = new AbilityMagnetism();
}