package org.zeith.improvableskills.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.zeith.improvableskills.api.evt.EntityEnchantmentLevelEvent;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@ModifyConstant(
			method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I",
			constant = @Constant(intValue = 0),
			slice = @Slice(
					from = @At("RETURN"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I")
			)
	)
	private static int getEnchantmentLevel_IS3(int var, Enchantment enchantment, LivingEntity entity)
	{
		if(entity != null)
		{
			var evt = new EntityEnchantmentLevelEvent(entity, enchantment);
			evt.max(var);
			MinecraftForge.EVENT_BUS.post(evt);
			return evt.getMax();
		}
		return var;
	}
}