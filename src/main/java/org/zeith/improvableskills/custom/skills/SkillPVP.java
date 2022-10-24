package org.zeith.improvableskills.custom.skills;

import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillPVP
		extends PlayerSkillBase
{
	public SkillPVP()
	{
		super(20);
		xpCalculator.xpValue = 2;
	}
}