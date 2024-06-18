package org.zeith.improvableskills.api.evt;

import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class EntityEnchantmentLevelEvent
		extends LivingEvent
{
	@Getter
	private int max;
	
	private final Holder<Enchantment> ench;
	
	public EntityEnchantmentLevelEvent(LivingEntity entity, Holder<Enchantment> ench)
	{
		super(entity);
		this.ench = ench;
	}
	
	public Enchantment getEnchantment()
	{
		return ench.value();
	}
	
	public Holder<Enchantment> enchantment()
	{
		return ench;
	}
	
	public void max(int with)
	{
		this.max = Math.max(max, with);
	}
}