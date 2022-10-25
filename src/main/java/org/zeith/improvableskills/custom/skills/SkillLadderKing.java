package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillLadderKing
		extends PlayerSkillBase
{
	public SkillLadderKing()
	{
		super(15);
		xpCalculator.xpValue = 2;
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		if(isActive && data.player.onClimbable() && !data.player.isShiftKeyDown())
		{
			float multiplier = data.getSkillLevel(this) / (float) maxLvl;
			if(!data.player.horizontalCollision)
				multiplier *= 0.5F;
			data.player.move(MoverType.SELF, new Vec3(0.0D, data.player.getDeltaMovement().y * multiplier, 0.0D));
		}
	}
}