package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.evt.HarvestDropsEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.api.treasures.*;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.SoundsIS;

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
		addListener(this::hook);
	}
	
	private void hook(HarvestDropsEvent e)
	{
		var pos = e.getPos();
		var level = e.getLevel();
		NonNullList<ItemStack> drops = e.getDrops();
		
		int ps = drops.size();
		
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			if(level instanceof ServerLevel mp && mp.getBlockState(pos).getMaterial() == Material.SAND && mp.getBiome(pos).get().getBaseTemperature() >= 2F)
			{
				RandomSource rng = data.player.getRandom();
				
				if(rng.nextInt(100) < 4 * data.getSkillLevel(this) && data.isSkillActive(this))
				{
					TreasureContext ctx = new TreasureContext.Builder()
							.withCaller(this)
							.withData(data)
							.withLocation(mp, pos)
							.withRNG(rng)
							.build();
					TreasureDropBase dr = DropUtil.chooseDrop(ctx);
					if(dr != null) dr.drop(ctx, drops);
				}
			}
		});
		
		if(drops.size() > ps)
		{
			level.playSound(null, e.getPos(), SoundsIS.TREASURE_FOUND, SoundSource.BLOCKS);
		}
	}
}