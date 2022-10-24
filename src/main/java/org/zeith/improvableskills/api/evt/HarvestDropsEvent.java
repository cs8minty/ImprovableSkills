package org.zeith.improvableskills.api.evt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

public class HarvestDropsEvent
		extends BlockEvent
{
	private final NonNullList<ItemStack> drops = NonNullList.create();
	private final Player entity;
	
	public HarvestDropsEvent(LevelAccessor level, BlockPos pos, BlockState state, Player entity)
	{
		super(level, pos, state);
		this.entity = entity;
	}
	
	public Player getEntity()
	{
		return entity;
	}
	
	public NonNullList<ItemStack> getDrops()
	{
		return drops;
	}
}