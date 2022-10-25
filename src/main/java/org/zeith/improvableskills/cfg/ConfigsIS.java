package org.zeith.improvableskills.cfg;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.zeith.hammerlib.annotations.SetupConfigs;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.List;

public class ConfigsIS
{
	public static ConfigFile config;
	private static List<String> la;
	
	public static boolean xpBank;
	public static boolean addBookToInv;
	
	@SetupConfigs
	public static void reloadCustom(ConfigFile cfgs)
	{
		config = cfgs;
		
		ConfigEntryCategory gameplay = cfgs.getCategory("Gameplay")
				.setDescription("Gameplay affecting features");
		{
			xpBank = gameplay.getBooleanEntry("XP Storage", true)
					.setDescription("Should XP Bank be active in the book? Disabling this only hides the skill from the player.")
					.getValue();
		}
		
		if(FMLEnvironment.dist == Dist.CLIENT)
		{
			ConfigEntryCategory clientSide = cfgs.getCategory("Client-side")
					.setDescription("Features that only matter when the mod is loaded on client.");
			{
				addBookToInv = clientSide.getBooleanEntry("Add Book to Inventory", true)
						.setDescription("Should ImprovableSkills add it's Book of Skills into player's inventory?")
						.getValue();
			}
		}
	}
	
	public static void reloadCost(PlayerSkillBase skill)
	{
		ConfigEntryCategory costs = config.getCategory("Gameplay")
				.setDescription("Gameplay affecting features")
				.getCategory("Costs")
				.setDescription("Configure how expensive each skill is");
		
		skill.xpCalculator.load(costs, skill.getRegistryName().toString().replace(":", "."));
		
		if(config.hasChanged())
			config.save();
	}
	
	public static boolean enableSkill(PlayerSkillBase skill, ResourceLocation id)
	{
		var value = config.getCategory("Skills")
				.setDescription("What skills should be enabled?")
				.getBooleanEntry(id.toString(), true)
				.setDescription("Should Skill \"" + skill.getUnlocalizedName(id) + "\" be added to the game?")
				.getValue();
		if(config.hasChanged()) config.save();
		return value;
	}
}