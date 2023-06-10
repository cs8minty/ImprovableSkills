package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.client.gui.PageletDiscord;
import org.zeith.improvableskills.custom.pagelets.*;

@SimplyRegister
public interface PageletsIS
{
	@RegistryName("skills")
	PageletSkills SKILLS = new PageletSkills();
	
	@RegistryName("abilities")
	PageletAbilities ABILITIES = new PageletAbilities();
	
	@RegistryName("xp_bank")
	@OnlyIf(owner = ConfigsIS.class, member = "xpBank")
	PageletXPStorage XP_STORAGE = new PageletXPStorage();
	
	@RegistryName("update")
	PageletUpdate UPDATE = new PageletUpdate();
	
	@RegistryName("news")
	PageletNews NEWS = new PageletNews();
	
	@RegistryName("discord")
	PageletDiscord DISCORD = new PageletDiscord();
}