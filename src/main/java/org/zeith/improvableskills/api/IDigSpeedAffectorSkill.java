package org.zeith.improvableskills.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface IDigSpeedAffectorSkill
{
	float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data);
}