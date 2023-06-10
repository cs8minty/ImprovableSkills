package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.custom.skills.*;

@SuppressWarnings("unused") // We don't care if the is a reference or not, it is there for registration purposes.
@SimplyRegister
public interface SkillsIS
{
	@RegistryName("accelerated_furnace")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillAcceleratedFurnace ACCELERATED_FURNACE = new SkillAcceleratedFurnace();
	
	@RegistryName("fast_swimmer")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillFastSwimmer FAST_SWIMMER = new SkillFastSwimmer();
	
	@RegistryName("leaper")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillLeaper LEAPER = new SkillLeaper();
	
	@RegistryName("ladder_king")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillLadderKing LADDER_KING = new SkillLadderKing();
	
	@RegistryName("soft_landing")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillSoftLanding SOFT_LANDING = new SkillSoftLanding();
	
	@RegistryName("attack_speed")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillAttackSpeed ATTACK_SPEED = new SkillAttackSpeed();
	
	@RegistryName("mining")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillMining MINING = new SkillMining();
	
	@RegistryName("digging")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillDigging DIGGING = new SkillDigging();
	
	@RegistryName("cutting")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillCutting CUTTING = new SkillCutting();
	
	@RegistryName("obsidian_skin")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillObsidianSkin OBSIDIAN_SKIN = new SkillObsidianSkin();
	
	@RegistryName("luck_of_the_sea")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillLuckOfTheSea LUCK_OF_THE_SEA = new SkillLuckOfTheSea();
	
	@RegistryName("health")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillHealth HEALTH = new SkillHealth();
	
	@RegistryName("growth")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillGrowth GROWTH = new SkillGrowth();
	
	@RegistryName("alchemist")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillAlchemist ALCHEMIST = new SkillAlchemist();
	
	@RegistryName("generic_protection")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillGenericProtection GENERIC_PROTECTION = new SkillGenericProtection();
	
	@RegistryName("treasure_sands")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillTreasureSands TREASURE_OF_SANDS = new SkillTreasureSands();
	
	@RegistryName("atkdmg_melee")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillAtkDmgMelee DAMAGE_MELEE = new SkillAtkDmgMelee();
	
	@RegistryName("atkdmg_ranged")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillAtkDmgRanged DAMAGE_RANGED = new SkillAtkDmgRanged();
	
	@RegistryName("pvp")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillPVP PVP = new SkillPVP();
	
	@RegistryName("enchanter")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillEnchanter ENCHANTER = new SkillEnchanter();
	
	@RegistryName("ender_manipulator")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillEnderManipulator ENDER_MANIPULATOR = new SkillEnderManipulator();
	
	@RegistryName("xp_plus")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillXPPlus XP_PLUS = new SkillXPPlus();
	
	@RegistryName("silent_foot")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillSilentFoot SILENT_FOOT = new SkillSilentFoot();
	
	@RegistryName("dexterous_arms")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillDexterousArms DEXTEROUS_ARMS = new SkillDexterousArms();
	
	@RegistryName("soul_speed")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillSoulSpeed SOUL_SPEED = new SkillSoulSpeed();
	
	@RegistryName("huckster")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillHuckster HUCKSTER = new SkillHuckster();
	
	@RegistryName("mob_repellent")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	SkillMobRepellent MOB_REPELLENT = new SkillMobRepellent();
}