package org.zeith.improvableskills.api.evt;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ApplySpecialPricesEvent
		extends LivingEvent
{
	private final Player player;
	
	public ApplySpecialPricesEvent(AbstractVillager villager, Player player)
	{
		super(villager);
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	@Override
	public AbstractVillager getEntity()
	{
		return (AbstractVillager) super.getEntity();
	}
}
