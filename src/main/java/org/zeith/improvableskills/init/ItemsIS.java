package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.custom.items.*;

@SimplyRegister
public interface ItemsIS
{
	@RegistryName("parchment_fragment")
	ItemParchmentFragment PARCHMENT_FRAGMENT = new ItemParchmentFragment();
	
	@RegistryName("skills_book")
	ItemSkillsBook SKILLS_BOOK = new ItemSkillsBook();
	
	@RegistryName("scroll_ability")
	ItemAbilityScroll ABILITY_SCROLL = new ItemAbilityScroll();
	
	@RegistryName("scroll_normal")
	ItemSkillScroll SKILL_SCROLL = new ItemSkillScroll();
	
	@RegistryName("scroll_creative")
	ItemCreativeSkillScroll CREATIVE_SKILL_SCROLL = new ItemCreativeSkillScroll();
}