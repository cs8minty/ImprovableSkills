package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.init.TagsIS3;

import java.util.ArrayList;
import java.util.List;

public class SkillGrowth
		extends PlayerSkillBase
{
	public SkillGrowth()
	{
		super(20);
		setupScroll();
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.JUNGLE_TEMPLE);
		setColor(0x00DA7B);
		xpCalculator.xpValue = 3;
		xpCalculator.setBaseFormula("((%lvl%)^%xpv%)*0.9+32");
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		short lvl;
		if(isActive && (lvl = data.getSkillLevel(this)) > 0 && data.player.tickCount % ((maxLvl - lvl) * 3 + 80) == 0)
			growAround(data.player, 2 + lvl / 4, (int) Math.sqrt(lvl) / 2 + 1);
	}
	
	public static void growAround(Player ent, int rad, int max)
	{
		var world = ent.level;
		List<BlockPos> positions = new ArrayList<>();
		
		for(int x = -rad; x <= rad; ++x)
			for(int z = -rad; z <= rad; ++z)
				for(int y = -rad / 2; y <= rad / 2; ++y)
				{
					BlockPos pos = ent.blockPosition().offset(x, y, z);
					BlockState state = world.getBlockState(pos);
					Block b = state.getBlock();
					if(b instanceof BonemealableBlock gr)
					{
						if(state.is(TagsIS3.Blocks.GROWTH_SKILL_BLOCKLIST)) continue;
						if(gr.isValidBonemealTarget(world, pos, state, world.isClientSide) && gr.isBonemealSuccess(world, world.random, pos, state))
							positions.add(pos);
					}
				}
		
		int co = Math.min(ent.level.random.nextInt(max), positions.size());
		for(int i = 0; i < co; ++i)
		{
			BlockPos pos = positions.remove(ent.level.random.nextInt(positions.size()));
			if(BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, ent))
				world.levelEvent(2005, pos, 0);
		}
	}
}