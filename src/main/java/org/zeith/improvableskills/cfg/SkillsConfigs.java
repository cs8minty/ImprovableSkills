package org.zeith.improvableskills.cfg;

import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.improvableskills.ImprovableSkills;

import java.io.File;

public class SkillsConfigs
{
	public static final int CUR_VERSION = 1;
	private static File configFile;
	
	public static void setConfigFile(File configFile)
	{
		if(SkillsConfigs.configFile == null)
		{
			SkillsConfigs.configFile = configFile;
			reloadSkillConfigs();
		}
	}
	
	public static void reloadSkillConfigs()
	{
		final ConfigFile config = new ConfigFile(configFile);
		
		boolean has$ = config.categories.containsKey("$");
		ConfigEntryCategory $ = config.getCategory("$");
		int version = $.getIntEntry("cfgversion", CUR_VERSION, 0, Integer.MAX_VALUE).getValue();
		if(!has$ || version != CUR_VERSION)
		{
			config.categories.clear();
			File old = new File(configFile.getAbsolutePath() + ".old");
			if(old.isFile())
				old.delete();
			config.config.renameTo(old);
			$ = config.getCategory("$");
			version = $.getIntEntry("cfgversion", CUR_VERSION, 0, Integer.MAX_VALUE).getValue();
		}
		
		ConfigEntryCategory costs = config.getCategory("Costs").setDescription("Configure how expensive each skill is");
		
		ImprovableSkills.SKILLS()
				.getValues()
				.forEach(skill -> skill.xpCalculator.load(costs.getCategory(skill.getRegistryName().toString().replace(":", "/")), skill.getRegistryName().toString().replace(":", ".")));
		
		config.save();
	}
}