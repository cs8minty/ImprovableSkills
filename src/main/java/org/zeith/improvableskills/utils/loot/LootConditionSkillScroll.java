package org.zeith.improvableskills.utils.loot;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraftforge.common.util.FakePlayer;
import org.zeith.improvableskills.api.loot.RandomBoolean;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class LootConditionSkillScroll
		implements LootItemCondition
{
	public RandomBoolean oneInN;
	private final PlayerSkillBase skill;
	
	public LootConditionSkillScroll(PlayerSkillBase skill, RandomBoolean oneInN)
	{
		this.skill = skill;
		this.oneInN = oneInN;
	}
	
	@Override
	public LootItemConditionType getType()
	{
		return LootItemConditions.RANDOM_CHANCE;
	}
	
	@Override
	public boolean test(LootContext context)
	{
		if(!oneInN.get(context.getRandom()))
			return false;
		
		Entity ent = context.hasParam(LootContextParams.KILLER_ENTITY) ? context.getParam(LootContextParams.KILLER_ENTITY) : null;
		if(ent == null && context.hasParam(LootContextParams.THIS_ENTITY)) ent = context.getParam(LootContextParams.THIS_ENTITY);
		
		if(ent instanceof Player p && !(ent instanceof FakePlayer))
			return PlayerDataManager.handleDataSafely(p, data -> !data.hasSkillScroll(skill), true);
		
		return false;
	}
}