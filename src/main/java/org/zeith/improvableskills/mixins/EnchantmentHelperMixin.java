package org.zeith.improvableskills.mixins;

import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.improvableskills.api.evt.EntityEnchantmentLevelEvent;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.SkillsIS;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@Inject(
			method = "getEnchantmentLevel",
			at = @At("RETURN"),
			cancellable = true
	)
	private static void IS3_getEnchantmentLevel(Holder<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir)
	{
		if(entity != null)
		{
			var evt = new EntityEnchantmentLevelEvent(entity, enchantment);
			evt.max(cir.getReturnValue());
			NeoForge.EVENT_BUS.post(evt);
			cir.setReturnValue(evt.getMax());
		}
	}
	
	@ModifyVariable(
			method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/item/ItemStack;getAllEnchantments(Lnet/minecraft/core/HolderLookup$RegistryLookup;)Lnet/minecraft/world/item/enchantment/ItemEnchantments;",
					shift = At.Shift.AFTER
			),
			index = 4
	)
	private static ItemEnchantments IS3_runIterationOnItem(ItemEnchantments value, ItemStack stack, EquipmentSlot slot, LivingEntity entity)
	{
		if(stack.is(ItemTags.FOOT_ARMOR_ENCHANTABLE) && entity instanceof Player pl && SkillsIS.SOUL_SPEED.getRegistryName() != null)
		{
			var enchReg = entity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
			var ss = enchReg.get(Enchantments.SOUL_SPEED).orElse(null);
			if(ss == null) return value;
			int ssl = value.getLevel(ss);
			var newSSL = PlayerDataManager.handleDataSafely(pl, data -> data.isSkillActive(SkillsIS.SOUL_SPEED) ? (int) data.getSkillLevel(SkillsIS.SOUL_SPEED) : 0, ssl);
			if(newSSL > ssl)
			{
				var mut = new ItemEnchantments.Mutable(value);
				mut.upgrade(ss, newSSL);
				value = mut.toImmutable();
			}
		}
		return value;
	}
}