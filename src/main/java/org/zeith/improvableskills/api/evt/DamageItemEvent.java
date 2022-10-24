package org.zeith.improvableskills.api.evt;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public class DamageItemEvent
		extends EntityEvent
{
	private final ItemStack item;
	private final int originalDamage;
	private int newDamage;
	
	public DamageItemEvent(ItemStack item, LivingEntity entity, int originalDamage)
	{
		super(entity);
		this.item = item;
		this.originalDamage = newDamage = originalDamage;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public int getOriginalDamage()
	{
		return originalDamage;
	}
	
	public int getNewDamage()
	{
		return newDamage;
	}
	
	public void setNewDamage(int newDamage)
	{
		this.newDamage = newDamage;
	}
	
	public RandomSource getRandom()
	{
		return Optional.ofNullable(getEntity()).map(LivingEntity::getRandom).orElseGet(RandomSource::create);
	}
	
	@Nullable
	@Override
	public LivingEntity getEntity()
	{
		return (LivingEntity) super.getEntity();
	}
}