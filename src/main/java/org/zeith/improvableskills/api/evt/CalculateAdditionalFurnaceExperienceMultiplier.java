package org.zeith.improvableskills.api.evt;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CalculateAdditionalFurnaceExperienceMultiplier
		extends PlayerEvent
{
	private float multiplier = 0;
	
	public CalculateAdditionalFurnaceExperienceMultiplier(Player player, AbstractFurnaceBlockEntity furnace)
	{
		super(player);
	}
	
	public float getMultiplier()
	{
		return multiplier;
	}
	
	public void addExtraPercent(float multiplier)
	{
		this.multiplier += multiplier;
	}
}