package org.zeith.improvableskills.proxy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.zeith.improvableskills.api.evt.DamageItemEvent;

import java.util.function.Consumer;

public class ASMProxy
{
	public static int hurtItem(ItemStack stack, int damageBy, LivingEntity entity, Consumer<LivingEntity> onBroken)
	{
		var evt = new DamageItemEvent(stack, entity, damageBy);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.getNewDamage();
	}
}