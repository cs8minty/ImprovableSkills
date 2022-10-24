package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.improvableskills.api.IDigSpeedAffectorSkill;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillMining
		extends PlayerSkillBase
		implements IDigSpeedAffectorSkill
{
	public SkillMining()
	{
		super(25);
		xpCalculator.setBaseFormula("sqrt(%lvl%^3)*3");
	}
	
	@Override
	public float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data)
	{
		if(pos == null)
			return 0F;
		BlockState b = data.player.level.getBlockState(pos);
		if(b.canHarvestBlock(data.player.level, pos, data.player) && b.is(BlockTags.MINEABLE_WITH_PICKAXE))
			return data.getSkillLevel(this) / 8F;
		return 0;
	}
}