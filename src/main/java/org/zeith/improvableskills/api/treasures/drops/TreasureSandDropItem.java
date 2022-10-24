package org.zeith.improvableskills.api.treasures.drops;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.zeith.improvableskills.api.treasures.TreasureContext;
import org.zeith.improvableskills.api.treasures.TreasureDropBase;
import org.zeith.improvableskills.init.SkillsIS;

import java.util.Arrays;
import java.util.List;

public class TreasureSandDropItem
		extends TreasureDropBase
{
	public final NonNullList<Stackable> items = NonNullList.create();
	public int minLvl;
	
	public TreasureSandDropItem()
	{
	}
	
	public TreasureSandDropItem(int lvl, Stackable... items)
	{
		this.minLvl = lvl;
		this.items.addAll(Arrays.asList(items));
	}
	
	public TreasureSandDropItem(int lvl, Object... items)
	{
		this.minLvl = lvl;
		
		for(int i = 0; i < items.length; ++i)
		{
			Object o = items[i];
			
			if(o == null)
				throw new NullPointerException("Item at index " + i + " is null.");
			
			if(o instanceof ItemLike l)
				this.items.add(Stackable.of(new ItemStack(l)));
			else if(o instanceof ItemStack)
				this.items.add(Stackable.of(((ItemStack) o).copy()));
			else if(o instanceof Stackable)
				this.items.add((Stackable) o);
			else
				throw new IllegalArgumentException("Item at index " + i + " is not supported!");
		}
	}
	
	@Override
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
		for(Stackable s : items)
			if(s != null)
				drops.add(s.transform(ctx.rand()));
	}
	
	@Override
	public TreasureDropBase copy()
	{
		TreasureSandDropItem l = (TreasureSandDropItem) super.copy();
		l.minLvl = minLvl;
		l.items.addAll(items);
		return this;
	}
	
	@Override
	public boolean canDrop(TreasureContext ctx)
	{
		return ctx.caller() == SkillsIS.TREASURE_OF_SANDS
				&& ctx.data().getSkillLevel(ctx.caller()) >= minLvl;
	}
}