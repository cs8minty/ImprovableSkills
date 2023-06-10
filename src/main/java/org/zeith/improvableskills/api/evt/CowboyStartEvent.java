package org.zeith.improvableskills.api.evt;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class CowboyStartEvent
		extends PlayerEvent
{
	private final LivingEntity target;
	
	public CowboyStartEvent(Player player, LivingEntity target)
	{
		super(player);
		this.target = target;
	}
	
	public LivingEntity target()
	{
		return target;
	}
}