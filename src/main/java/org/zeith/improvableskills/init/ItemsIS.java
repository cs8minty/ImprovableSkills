package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.custom.items.*;

@SimplyRegister
public class ItemsIS
{
	@RegistryName("parchment_fragment")
	public static final ItemParchmentFragment PARCHMENT_FRAGMENT = new ItemParchmentFragment();
	
	@RegistryName("skills_book")
	public static final ItemSkillsBook SKILLS_BOOK = new ItemSkillsBook();
	
	@RegistryName("scroll_ability")
	public static final ItemAbilityScroll ABILITY_SCROLL = new ItemAbilityScroll();
	
	@RegistryName("scroll_normal")
	public static final ItemSkillScroll SKILL_SCROLL = new ItemSkillScroll();
	
	@RegistryName("scroll_creative")
	public static final ItemCreativeSkillScroll CREATIVE_SKILL_SCROLL = new ItemCreativeSkillScroll();
}