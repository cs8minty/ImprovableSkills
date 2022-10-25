package org.zeith.improvableskills.init;

import org.zeith.hammerlib.annotations.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.custom.skills.*;

@SuppressWarnings("unused") // We don't care if the is a reference or not, it is there for registration purposes.
@SimplyRegister
public class SkillsIS
{
	@RegistryName("accelerated_furnace")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillAcceleratedFurnace ACCELERATED_FURNACE = new SkillAcceleratedFurnace();
	
	@RegistryName("fast_swimmer")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillFastSwimmer FAST_SWIMMER = new SkillFastSwimmer();
	
	@RegistryName("leaper")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillLeaper LEAPER = new SkillLeaper();
	
	@RegistryName("ladder_king")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillLadderKing LADDER_KING = new SkillLadderKing();
	
	@RegistryName("soft_landing")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillSoftLanding SOFT_LANDING = new SkillSoftLanding();
	
	@RegistryName("attack_speed")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillAttackSpeed ATTACK_SPEED = new SkillAttackSpeed();
	
	@RegistryName("mining")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillMining MINING = new SkillMining();
	
	@RegistryName("digging")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillDigging DIGGING = new SkillDigging();
	
	@RegistryName("cutting")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillCutting CUTTING = new SkillCutting();
	
	@RegistryName("obsidian_skin")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillObsidianSkin OBSIDIAN_SKIN = new SkillObsidianSkin();
	
	@RegistryName("luck_of_the_sea")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillLuckOfTheSea LUCK_OF_THE_SEA = new SkillLuckOfTheSea();
	
	@RegistryName("health")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillHealth HEALTH = new SkillHealth();
	
	@RegistryName("growth")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillGrowth GROWTH = new SkillGrowth();
	
	@RegistryName("alchemist")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillAlchemist ALCHEMIST = new SkillAlchemist();
	
	@RegistryName("generic_protection")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillGenericProtection GENERIC_PROTECTION = new SkillGenericProtection();
	
	@RegistryName("treasure_sands")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillTreasureSands TREASURE_OF_SANDS = new SkillTreasureSands();
	
	@RegistryName("atkdmg_melee")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillAtkDmgMelee DAMAGE_MELEE = new SkillAtkDmgMelee();
	
	@RegistryName("atkdmg_ranged")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillAtkDmgRanged DAMAGE_RANGED = new SkillAtkDmgRanged();
	
	@RegistryName("pvp")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillPVP PVP = new SkillPVP();
	
	@RegistryName("enchanter")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillEnchanter ENCHANTER = new SkillEnchanter();
	
	@RegistryName("ender_manipulator")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillEnderManipulator ENDER_MANIPULATOR = new SkillEnderManipulator();
	
	@RegistryName("xp_plus")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillXPPlus XP_PLUS = new SkillXPPlus();
	
	@RegistryName("silent_foot")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillSilentFoot SILENT_FOOT = new SkillSilentFoot();
	
	@RegistryName("dexterous_arms")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillDexterousArms DEXTEROUS_ARMS = new SkillDexterousArms();
	
	@RegistryName("soul_speed")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillSoulSpeed SOUL_SPEED = new SkillSoulSpeed();
	
	@RegistryName("huckster")
	@OnlyIf(owner = ConfigsIS.class, member = "enableSkill")
	public static final SkillHuckster HUCKSTER = new SkillHuckster();
}