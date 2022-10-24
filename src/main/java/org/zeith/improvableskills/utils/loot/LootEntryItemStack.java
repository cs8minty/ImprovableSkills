package org.zeith.improvableskills.utils.loot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

public class LootEntryItemStack
		extends LootPoolSingletonContainer
{
	protected final ItemStack item;
	
	public LootEntryItemStack(ItemStack itemIn, int weightIn, int qualityIn, LootItemFunction[] functionsIn, LootItemCondition[] conditionsIn)
	{
		super(weightIn, qualityIn, conditionsIn, functionsIn);
		this.item = itemIn;
	}
	
	public static EntryConstructor build(ItemStack drop)
	{
		return (weight, quality, conditions, functions) -> new LootEntryItemStack(drop, weight, quality, functions, conditions);
	}
	
	@Override
	protected void createItemStack(Consumer<ItemStack> handler, LootContext ctx)
	{
		ItemStack item = this.item.copy();
		
		for(LootItemFunction function : this.functions)
		{
			function.apply(item, ctx);
		}
		
		handler.accept(item);
	}
	
	@Override
	public LootPoolEntryType getType()
	{
		return LootPoolEntries.ITEM;
	}
}