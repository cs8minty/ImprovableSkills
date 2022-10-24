package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.EntityType;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillAtkDmgRanged
		extends PlayerSkillBase
{
	public SkillAtkDmgRanged()
	{
		super(15);
		lockedWithScroll = true;
		generateScroll = true;
		
		getLoot().chance.n = 40;
		getLoot().setLootTable(EntityType.SKELETON.getDefaultLootTable());
		
		xpCalculator.xpValue = 3;
	}
}