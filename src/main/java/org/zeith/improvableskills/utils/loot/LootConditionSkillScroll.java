package org.zeith.improvableskills.utils.loot;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraftforge.common.util.FakePlayer;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class LootConditionSkillScroll
		extends LootItemRandomChanceCondition
{
	private final PlayerSkillBase skill;
	
	public LootConditionSkillScroll(float probability, PlayerSkillBase skill)
	{
		super(probability);
		this.skill = skill;
	}
	
	public Component getContextComponent()
	{
		return skill.getLocalizedName();
	}
	
	@Override
	public LootItemConditionType getType()
	{
		return LootItemConditions.RANDOM_CHANCE;
	}
	
	@Override
	public boolean test(LootContext context)
	{
		Entity ent = context.hasParam(LootContextParams.KILLER_ENTITY) ? context.getParam(LootContextParams.KILLER_ENTITY) : null;
		if(ent == null && context.hasParam(LootContextParams.THIS_ENTITY)) ent = context.getParam(LootContextParams.THIS_ENTITY);
		
		if(ent != null && !(ent instanceof Player))
		{
			var player = ent.level().getNearestPlayer(ent, 32);
			if(player != null) ent = player;
		}
		
		if(ent instanceof Player p && !(ent instanceof FakePlayer))
			return PlayerDataManager.handleDataSafely(p, data -> !data.hasSkillScroll(skill), true);
		
		return false;
	}
}