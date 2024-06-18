package org.zeith.improvableskills.api.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.zeith.hammerlib.mixins.LootTableAccessor;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.custom.items.data.StoredSkill;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.utils.loot.LootConditionSkillScroll;

import java.util.HashSet;
import java.util.Set;

public class SkillLoot
{
	public final PlayerSkillBase skill;
	private final Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
	public RandomBoolean chance = new RandomBoolean();
	
	/**
	 * Setting this to true will remove any other loot from being generated if a ability scroll drops in a loot table.
	 */
	public boolean exclusive;
	
	public SkillLoot(PlayerSkillBase skill)
	{
		this.skill = skill;
	}
	
	public void setLootTable(ResourceKey<LootTable> rl)
	{
		lootTables.clear();
		lootTables.add(rl);
	}
	
	@SafeVarargs
	public final void setLootTables(ResourceKey<LootTable>... rl)
	{
		lootTables.clear();
		lootTables.addAll(Set.of(rl));
	}
	
	public void addLootTable(ResourceKey<LootTable> rl)
	{
		lootTables.add(rl);
	}
	
	@SafeVarargs
	public final void addLootTables(ResourceKey<LootTable>... rl)
	{
		lootTables.addAll(Set.of(rl));
	}
	
	public void apply(ResourceKey<LootTable> id, LootTable table)
	{
		if(!lootTables.contains(id)) return;
		ImprovableSkills.LOG.info("Injecting scroll for ability '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getLootTableId() + "'!");
		
		try
		{
			var entry = LootItem.lootTableItem(ItemsIS.SKILL_SCROLL)
					.apply(SetComponentsFunction.setComponent(StoredSkill.TYPE.get(), new StoredSkill(skill)))
					.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)));
			
			var pools = ((LootTableAccessor) table).getPools();
			
			pools.add(LootPool.lootPool()
					.when(() -> new LootConditionSkillScroll(ConstantValue.exactly(1F), skill))
					.setRolls(ConstantValue.exactly(1F))
					.add(EmptyLootItem.emptyItem().setWeight(chance.n - 1))
					.add(entry.setWeight(1).setQuality(60))
					.name(skill.getRegistryName().toString() + "_skill_scroll")
					.build()
			);
		} catch(Throwable err)
		{
			ImprovableSkills.LOG.error("Failed to inject scroll for ability '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getLootTableId() + "'!!!");
			err.printStackTrace();
		}
	}
}