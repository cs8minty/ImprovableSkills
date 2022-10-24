package org.zeith.improvableskills.api.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.utils.loot.LootConditionSkillScroll;

import java.util.List;
import java.util.function.Predicate;

public class SkillLoot
{
	public final PlayerSkillBase skill;
	public Predicate<ResourceLocation> lootTableChecker = r -> false;
	public RandomBoolean chance = new RandomBoolean();
	
	public SkillLoot(PlayerSkillBase skill)
	{
		this.skill = skill;
	}
	
	public void setLootTable(ResourceLocation rl)
	{
		lootTableChecker = r -> r.equals(rl);
	}
	
	public void setLootTables(ResourceLocation... rl)
	{
		var rls = List.of(rl);
		lootTableChecker = rls::contains;
	}
	
	public void addLootTable(ResourceLocation rl)
	{
		lootTableChecker = lootTableChecker.or(rl::equals);
	}
	
	public void addLootTables(ResourceLocation... rl)
	{
		var rls = List.of(rl);
		lootTableChecker = lootTableChecker.or(rls::contains);
	}
	
	public void apply(LootTableLoadEvent table)
	{
		if(lootTableChecker != null && lootTableChecker.test(table.getName()))
		{
			ImprovableSkills.LOG.info("Injecting scroll for skill '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getName() + "'!");
			
			try
			{
				var entry =
						LootItem.lootTableItem(ItemsIS.SKILL_SCROLL)
								.apply(SetNbtFunction.setTag(ItemSkillScroll.of(skill).getTag()))
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)));
				
				table.getTable().addPool(
						LootPool.lootPool()
								.when(() -> new LootConditionSkillScroll(1F, skill))
								.setRolls(ConstantValue.exactly(1F))
								.add(EmptyLootItem.emptyItem().setWeight(chance.n - 1))
								.add(entry.setWeight(1).setQuality(60))
								.name(skill.getRegistryName().toString() + "_skill_scroll")
								.build()
				);
			} catch(Throwable err)
			{
				ImprovableSkills.LOG.error("Failed to inject scroll for skill '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getName() + "'!!!");
				err.printStackTrace();
			}
		}
	}
}