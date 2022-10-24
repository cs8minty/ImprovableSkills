package org.zeith.improvableskills.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.zeith.hammerlib.util.java.Cast;

import java.util.ArrayList;
import java.util.List;

public class TileHelper
{
	public static <T> List<T> collectTiles(Level world, BlockPos center, int rad, Class<T> type)
	{
		List<T> al = new ArrayList<>();
		for(int x = -rad; x <= rad; ++x)
			for(int y = -rad; y <= rad; ++y)
				for(int z = -rad; z <= rad; ++z)
				{
					T t = Cast.cast(world.getBlockEntity(center.offset(x, y, z)), type);
					if(t != null)
						al.add(t);
				}
		
		return al;
	}
}