package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.api.treasures.*;

import java.util.List;

public class SkillTreasureSands
		extends PlayerSkillBase
{
	public SkillTreasureSands()
	{
		super(3);
		setupScroll();
		getLoot().chance.n = 8;
		getLoot().setLootTable(BuiltInLootTables.DESERT_PYRAMID);
		setColor(0xCCAC57);
		xpCalculator.setBaseFormula("(%lvl%+1)^7+200");
	}
	
	public void handleDropAdd(LevelAccessor level, BlockPos pos, PlayerSkillData data, List<ItemStack> drops)
	{
		if(data != null && level instanceof ServerLevel sl)
		{
			if(level.getBlockState(pos).getMaterial() == Material.SAND && sl.getBiome(pos).get().getBaseTemperature() >= 2F)
			{
				RandomSource rng = data.player.getRandom();
				
				if(rng.nextInt(100) < 5 * data.getSkillLevel(this))
				{
					TreasureContext ctx = new TreasureContext.Builder()
							.withCaller(this)
							.withData(data)
							.withLocation(sl, pos)
							.withRNG(rng)
							.build();
					TreasureDropBase dr = DropUtil.chooseDrop(ctx);
					if(dr != null)
						dr.drop(ctx, drops);
				}
			}
		}
	}
}