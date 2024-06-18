package org.zeith.improvableskills.mixins;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public interface EnchantmentMenuAccessor
{
	@Invoker
	List<EnchantmentInstance> callGetEnchantmentList(RegistryAccess p_345264_, ItemStack p_39472_, int p_39473_, int p_39474_);
}
