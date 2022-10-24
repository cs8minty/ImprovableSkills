package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.client.gui.PageletDiscord;
import org.zeith.improvableskills.custom.pagelets.*;

@SimplyRegister
public class PageletsIS
{
	@RegistryName("skills")
	public static final PageletSkills SKILLS = new PageletSkills();
	
	@RegistryName("abilities")
	public static final PageletAbilities ABILITIES = new PageletAbilities();
	
	@RegistryName("xp_bank")
	@OnlyIf(owner = ConfigsIS.class, member = "xpBank")
	public static final PageletXPStorage XP_STORAGE = new PageletXPStorage();
	
	@RegistryName("update")
	public static final PageletUpdate UPDATE = new PageletUpdate();
	
	@RegistryName("news")
	public static final PageletNews NEWS = new PageletNews();
	
	@RegistryName("discord")
	public static final PageletDiscord DISCORD = new PageletDiscord();
}