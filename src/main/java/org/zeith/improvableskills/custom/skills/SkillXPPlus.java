package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.EntityType;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillXPPlus
		extends PlayerSkillBase
{
	public SkillXPPlus()
	{
		super(10);
		lockedWithScroll = true;
		generateScroll = true;
		getLoot().chance.n = 3;
		getLoot().setLootTable(EntityType.ELDER_GUARDIAN.getDefaultLootTable());
		setColor(0x93DA34);
		xpCalculator.setBaseFormula("%lvl%^3+(%lvl%+1)*100");
	}
}