package org.zeith.improvableskills.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.zeith.hammerlib.event.data.DataPackRegistryLoadEvent;
import org.zeith.hammerlib.mixins.LootTableAccessor;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.init.ItemsIS;

@EventBusSubscriber
public class LootTableLoader
{
	@SubscribeEvent
	public static void inspectLootTables(DataPackRegistryLoadEvent e)
	{
		e.getRegistry(Registries.LOOT_TABLE).ifPresent(reg ->
		{
			for(var en : reg.entrySet())
			{
				loadTable(en.getKey(), en.getValue());
			}
		});
	}
	
	public static void loadTable(ResourceKey<LootTable> id, LootTable table)
	{
		for(PlayerSkillBase skill : ImprovableSkills.SKILLS)
		{
			var lt = skill.getLoot();
			if(lt == null) continue;
			
			lt.apply(id, table);
		}
		
		if(id.location().getPath().contains("chests/") && ConfigsIS.parchmentGeneration)
		{
			if(ConfigsIS.blockedParchmentChests.contains(id.toString()))
			{
				ImprovableSkills.LOG.debug("SKIPPING parchment injection for LootTable '" + table.getLootTableId() + "'!");
				return;
			}
			ImprovableSkills.LOG.info("Injecting parchment into LootTable '" + table.getLootTableId() + "'!");
			
			try
			{
				var pools = ((LootTableAccessor) table).getPools();
				
				pools.add(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1F))
						.add(EmptyLootItem.emptyItem().setWeight(ConfigsIS.parchmentRarity))
						.add(LootItem.lootTableItem(ItemsIS.PARCHMENT_FRAGMENT)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)))
								.setWeight(1)
								.setQuality(60)
						)
						.name("parchment_fragment")
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