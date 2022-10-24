package org.zeith.improvableskills.custom.skills;

import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillSoftLanding
		extends PlayerSkillBase
{
	public SkillSoftLanding()
	{
		super(10);
		xpCalculator.xpValue = 2;
	}
}