package org.zeith.improvableskills.api.evt;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingEvent;

public class EntityEnchantmentLevelEvent
		extends LivingEvent
{
	private int max;
	
	private final Enchantment ench;
	
	public EntityEnchantmentLevelEvent(LivingEntity entity, Enchantment ench)
	{
		super(entity);
		this.ench = ench;
	}
	
	public Enchantment getEnchantment()
	{
		return ench;
	}
	
	public void max(int with)
	{
		this.max = Math.max(max, with);
	}
	
	public int getMax()
	{
		return max;
	}
}