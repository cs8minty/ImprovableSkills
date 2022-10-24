package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.improvableskills.api.IDigSpeedAffectorSkill;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillDigging
		extends PlayerSkillBase
		implements IDigSpeedAffectorSkill
{
	public SkillDigging()
	{
		super(25);
		xpCalculator.setBaseFormula("%lvl%^1.5");
	}
	
	@Override
	public float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data)
	{
		if(pos == null)
			return 0F;
		BlockState b = data.player.level.getBlockState(pos);
		if(b.canHarvestBlock(data.player.level, pos, data.player) && b.is(BlockTags.MINEABLE_WITH_SHOVEL))
			return data.getSkillLevel(this) / 28F;
		return 0;
	}
}