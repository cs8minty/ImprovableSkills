package org.zeith.improvableskills.cfg;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.zeith.hammerlib.annotations.SetupConfigs;
import org.zeith.hammerlib.util.configured.ConfigFile;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.types.ConfigCategory;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class ConfigsIS
{
	public static ConfigFile config;
	
	private static ConfigCategory gameplay;
	
	public static boolean xpBank;
	public static boolean addBookToInv;
	
	@SetupConfigs
	public static void reloadCustom(ConfigFile cfgs)
	{
		config = cfgs;
		
		gameplay = cfgs.setupCategory("Gameplay")
				.withComment("Gameplay affecting features");
		{
			xpBank = gameplay.getElement(ConfiguredLib.BOOLEAN, "XP Storage")
					.withDefault(true)
					.withComment("Should XP Bank be active in the book? Disabling this only hides the skill from the player.")
					.getValue();
		}
		
		if(FMLEnvironment.dist == Dist.CLIENT)
		{
			var clientSide = cfgs.setupCategory("Client-side")
					.withComment("Features that only matter when the mod is loaded on client.");
			{
				addBookToInv = clientSide.getElement(ConfiguredLib.BOOLEAN, "Add Book to Inventory")
						.withDefault(true)
						.withComment("Should ImprovableSkills add it's Book of Skills into player's inventory?")
						.getValue();
			}
		}
	}
	
	public static void reloadCosts()
	{
		ConfigCategory costs = gameplay.setupSubCategory("Costs")
				.withComment("Configure how expensive each skill is");
		
		for(PlayerSkillBase skill : ImprovableSkills.SKILLS())
		{
			skill.xpCalculator.load(costs, skill.getRegistryName().toString().replace(":", "_"));
		}
	}
	
	public static boolean enableSkill(PlayerSkillBase skill, ResourceLocation id)
	{
		return config.setupCategory("Skills")
				.withComment("What skills should be enabled?")
				.getElement(ConfiguredLib.BOOLEAN, id.toString())
				.withDefault(true)
				.withComment("Should Skill \"" + skill.getUnlocalizedName(id) + "\" be added to the game?")
				.getValue();
	}
}