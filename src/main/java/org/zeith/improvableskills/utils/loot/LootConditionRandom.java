package org.zeith.improvableskills.utils.loot;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import org.zeith.improvableskills.api.loot.RandomBoolean;

public class LootConditionRandom
		implements LootItemCondition
{
	public RandomBoolean oneInN;
	
	public LootConditionRandom(RandomBoolean oneInN)
	{
		this.oneInN = oneInN;
	}
	
	@Override
	public LootItemConditionType getType()
	{
		return LootItemConditions.RANDOM_CHANCE;
	}
	
	@Override
	public boolean test(LootContext lootContext)
	{
		return oneInN.get(lootContext.getRandom());
	}
}