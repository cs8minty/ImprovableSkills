package org.zeith.improvableskills.utils.loot;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
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
	
	private Player findPlayer(LootContext context, LootContextParam<Entity> par)
	{
		return context.hasParam(par) && context.getParamOrNull(par) instanceof Player pl && !(pl instanceof FakePlayer) ? pl : null;
	}
	
	@Override
	public boolean test(LootContext context)
	{
		Player p = findPlayer(context, LootContextParams.KILLER_ENTITY);
		if(p == null) p = findPlayer(context, LootContextParams.THIS_ENTITY);
		if(p == null) p = findPlayer(context, LootContextParams.DIRECT_KILLER_ENTITY);
		
		if(p != null)
			return PlayerDataManager.handleDataSafely(p, data -> !data.hasSkillScroll(skill) || data.getSkillProgress(skill) < 1F, true);
		
		return false;
	}
}