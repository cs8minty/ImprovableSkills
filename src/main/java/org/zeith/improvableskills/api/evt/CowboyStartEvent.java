package org.zeith.improvableskills.api.evt;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CowboyStartEvent
		extends PlayerEvent
		implements ICancellableEvent
{
	@Getter @Setter
	protected Result result = Result.DEFAULT;
	
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
	
	public enum Result
	{
		/**
		 * Forcibly allows the despawn to occur.
		 */
		ALLOW,
		
		/**
		 * The default logic in {@link Mob#checkDespawn()} will be used to determine if the despawn may occur.
		 */
		DEFAULT,
		
		/**
		 * Forcibly prevents the despawn from occurring.
		 */
		DENY;
	}
}