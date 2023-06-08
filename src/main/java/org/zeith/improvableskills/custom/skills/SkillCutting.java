package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.improvableskills.api.IDigSpeedAffectorSkill;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillCutting
		extends PlayerSkillBase
		implements IDigSpeedAffectorSkill
{
	public SkillCutting()
	{
		super(25);
		xpCalculator.setBaseFormula("%lvl%^1.5");
	}
	
	@Override
	public float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data)
	{
		if(pos == null)
			return 0F;
		BlockState b = data.player.level().getBlockState(pos);
		if(b.canHarvestBlock(data.player.level(), pos, data.player)
				&& (b.is(BlockTags.MINEABLE_WITH_AXE) || b.is(BlockTags.MINEABLE_WITH_HOE))
				&& data.player.getMainHandItem().getDestroySpeed(b) > 0)
			return data.getSkillLevel(this) / 8F;
		return 0;
	}
}