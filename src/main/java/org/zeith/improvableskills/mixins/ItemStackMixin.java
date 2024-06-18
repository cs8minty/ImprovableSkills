package org.zeith.improvableskills.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.proxy.ASMProxy;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin
{
	@ModifyVariable(
			method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"),
			index = 1,
			argsOnly = true
	)
	private int IS3_hurtAndBreak(int value, int i, ServerLevel level, @Nullable LivingEntity entity, Consumer<Item> handler)
	{
		return ASMProxy.hurtItem(Cast.cast(this), value, entity);
	}
}