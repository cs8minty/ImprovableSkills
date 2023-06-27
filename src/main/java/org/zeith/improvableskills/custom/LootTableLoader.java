package org.zeith.improvableskills.custom;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.loot.RandomBoolean;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.mixins.LootTableAccessor;

public class LootTableLoader
{
	public static void loadTable(LootTable table)
	{
		for(PlayerSkillBase skill : ImprovableSkills.SKILLS())
		{
			var lt = skill.getLoot();
			if(lt == null) continue;
			
			lt.apply(table);
		}
		
		if(table.getLootTableId().toString().toLowerCase().contains("chests/"))
		{
			RandomBoolean bool = new RandomBoolean();
			bool.n = 5;
			
			ImprovableSkills.LOG.info("Injecting parchment into LootTable '" + table.getLootTableId() + "'!");
			
			try
			{
				var pools = ((LootTableAccessor) table).getPools();
				
				pools.add(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1F))
						.add(EmptyLootItem.emptyItem().setWeight(4))
						.add(LootItem.lootTableItem(ItemsIS.PARCHMENT_FRAGMENT)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)))
								.setWeight(1)
								.setQuality(60)
						)
//						.name("parchment_fragment")
						.build());
			} catch(Throwable err)
			{
				ImprovableSkills.LOG.error("Failed to inject parchment into LootTable '" + table.getLootTableId() + "'!!!");
				err.printStackTrace();
			}
		}
	}
}