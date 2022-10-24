package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillSilentFoot
		extends PlayerSkillBase
{
	public SkillSilentFoot()
	{
		super(10);
		setupScroll();
		getLoot().chance.n = 1;
		getLoot().setLootTables(BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY_ICE_BOX);
		setColor(0x027978);
		xpCalculator.xpValue = 4;
		xpCalculator.setBaseFormula("((%lvl%+1)^%xpv%)/3");
	}
}