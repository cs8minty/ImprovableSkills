package org.zeith.improvableskills.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraftforge.common.MinecraftForge;
import org.zeith.improvableskills.api.evt.DamageItemEvent;
import org.zeith.improvableskills.api.evt.VibrationEvent;

import java.util.function.Consumer;

public class ASMProxy
{
	public static int hurtItem(ItemStack stack, int damageBy, LivingEntity entity, Consumer<LivingEntity> onBroken)
	{
		var evt = new DamageItemEvent(stack, entity, damageBy);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.getNewDamage();
	}
	
	public static boolean cancelVibrationReception(ServerLevel level, VibrationSystem.Data event, VibrationSystem.User context, VibrationInfo info)
	{
		if(MinecraftForge.EVENT_BUS.post(new VibrationEvent(level, event, context, info)))
		{
			event.setCurrentVibration(null);
			return true;
		}
		return false;
	}
}