package org.zeith.improvableskills.custom.skills;

import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillAttackSpeed
		extends PlayerSkillBase
{
	public SkillAttackSpeed()
	{
		super(25);
		xpCalculator.setBaseFormula("%lvl%^1.5");
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		data.player.attackStrengthTicker += Math.sqrt(data.getSkillLevel(this)) / 2;
	}
}