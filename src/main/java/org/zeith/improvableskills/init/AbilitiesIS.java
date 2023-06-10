package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.custom.abilities.*;

@SimplyRegister
public interface AbilitiesIS
{
	@RegistryName("enchanting")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityEnchanting ENCHANTING = new AbilityEnchanting();
	
	@RegistryName("crafter")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityCrafter CRAFTER = new AbilityCrafter();
	
	@RegistryName("anvil")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityAnvil ANVIL = new AbilityAnvil();
	
	@RegistryName("magnetism")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityMagnetism MAGNETISM = new AbilityMagnetism();
	
	@RegistryName("auto_xp_bank")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityAutoXpBank AUTO_XP_BANK = new AbilityAutoXpBank();
	
	@RegistryName("cowboy")
	@OnlyIf(owner = ConfigsIS.class, member = "enableAbility")
	AbilityCowboy COWBOY = new AbilityCowboy();
}