package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.EntityType;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillEnderManipulator
		extends PlayerSkillBase
{
	public SkillEnderManipulator()
	{
		super(5);
		setupScroll();
		getLoot().chance.n = 20;
		getLoot().setLootTable(EntityType.ENDERMAN.getDefaultLootTable());
		setColor(0xD5DA94);
		xpCalculator.xpValue = 3;
	}
}