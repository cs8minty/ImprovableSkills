package org.zeith.improvableskills.custom.skills;

import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillAtkDmgMelee
		extends PlayerSkillBase
{
	public SkillAtkDmgMelee()
	{
		super(15);
		xpCalculator.xpValue = 3;
	}
}