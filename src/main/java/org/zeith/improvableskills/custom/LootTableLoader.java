package org.zeith.improvableskills.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.zeith.hammerlib.core.adapter.LootTableAdapter;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.init.ItemsIS;

public class LootTableLoader
{
	public static void loadTable(ResourceLocation id, LootTable table)
	{
		for(PlayerSkillBase skill : ImprovableSkills.SKILLS())
		{
			var lt = skill.getLoot();
			if(lt == null) continue;
			
			lt.apply(id, table);
		}
		
		if(id.getPath().contains("chests/") && ConfigsIS.parchmentGeneration)
		{
			if(ConfigsIS.blockedParchmentChests.contains(id.toString()))
			{
				ImprovableSkills.LOG.debug("SKIPPING parchment injection for LootTable '" + table.getLootTableId() + "'!");
				return;
			}
			ImprovableSkills.LOG.info("Injecting parchment into LootTable '" + table.getLootTableId() + "'!");
			
			try
			{
				var pools = LootTableAdapter.getPools(table);
				
				pools.add(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1F))
						.add(EmptyLootItem.emptyItem().setWeight(ConfigsIS.parchmentRarity))
						.add(LootItem.lootTableItem(ItemsIS.PARCHMENT_FRAGMENT)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)))
								.setWeight(1)
								.setQuality(60)
						)
//						.name("parchment_fragment")
						.build());
			} catch(Throwable err)
			{
				ImprovableSkills.LOG.error(
						"Failed to inject parchment into LootTable '" + table.getLootTableId() + "'!!!");
				err.printStackTrace();
			}
		}
	}
}