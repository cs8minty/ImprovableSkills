package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillEnchanter
		extends PlayerSkillBase
{
	public SkillEnchanter()
	{
		super(20);
		lockedWithScroll = true;
		generateScroll = true;
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.STRONGHOLD_LIBRARY);
		setColor(0xFF179A);
		xpCalculator.xpValue = 2;
	}
}