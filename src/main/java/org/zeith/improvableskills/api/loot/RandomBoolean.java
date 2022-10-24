package org.zeith.improvableskills.api.loot;

import net.minecraft.util.RandomSource;

public class RandomBoolean
{
	public RandomSource rand;
	public int n;
	
	public RandomBoolean(RandomSource rand)
	{
		this.rand = rand;
	}
	
	public RandomBoolean()
	{
		this(RandomSource.create());
	}
	
	public boolean get()
	{
		return rand.nextInt(n) == 0;
	}
	
	public boolean get(RandomSource rand)
	{
		return rand.nextInt(n) == 0;
	}
}