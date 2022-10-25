package org.zeith.improvableskills.api.treasures;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class DropUtil
{
	public static RandomSource RANDOM = RandomSource.create();
	
	public static TreasureDropBase chooseDrop(TreasureContext ctx)
	{
		RANDOM = ctx.rand();
		return chooseDrop(TreasureRegistry.allDrops(), ctx);
	}
	
	public static TreasureDropBase chooseDrop(List<TreasureDropBase> allDrops, TreasureContext ctx)
	{
		List<TreasureDropBase> preDrops = new ArrayList<>();
		for(TreasureDropBase d : allDrops)
			if(d.canDrop(ctx))
				preDrops.add(d.copy());
		float weightTotal = 0F;
		ArrayList<Float> weightPoints = new ArrayList<>();
		weightPoints.add(0F);
		for(TreasureDropBase drop : preDrops)
		{
			weightTotal += drop.getChance() * 100;
			weightPoints.add(weightTotal);
		}
		float randomIndex = RANDOM.nextFloat() * weightTotal;
		return getDropByWeight(preDrops, weightPoints, randomIndex);
	}
	
	private static TreasureDropBase getDropByWeight(List<TreasureDropBase> drops, ArrayList<Float> weightPoints, float randomIndex)
	{
		for(int a = 0; a < drops.size(); a++)
			if((randomIndex >= weightPoints.get(a)) && (randomIndex < weightPoints.get(a + 1)))
				return drops.get(a);
		return null;
	}
}
