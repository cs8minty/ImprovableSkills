package org.zeith.improvableskills.api.treasures.drops;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface Stackable
{
	ItemStack transform(RandomSource rand);
	
	static Stackable of(ItemStack instance)
	{
		return r -> instance.copy();
	}
	
	static Stackable of(ItemStack instance, int min, int max)
	{
		int mult = instance.getCount();
		return r ->
		{
			ItemStack ns = instance.copy();
			ns.setCount(mult * min + r.nextInt(max - min + 1));
			return ns;
		};
	}
}