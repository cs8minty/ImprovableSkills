package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillObsidianSkin
		extends PlayerSkillBase
{
	public SkillObsidianSkin()
	{
		super(20);
		setupScroll();
		getLoot().chance.n = 3;
		getLoot().setLootTable(BuiltInLootTables.NETHER_BRIDGE);
		setColor(0x9B3EC9);
		xpCalculator.xpValue = 2;
	}
}