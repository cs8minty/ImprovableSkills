package org.zeith.improvableskills.custom.skills;

import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillLeaper
		extends PlayerSkillBase
{
	public SkillLeaper()
	{
		super(15);
		
		xpCalculator.xpValue = 2;
	}
}