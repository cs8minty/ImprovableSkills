package org.zeith.improvableskills.init;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.client.gui.abil.anvil.AnvilMenuPortable;
import org.zeith.improvableskills.client.gui.abil.crafting.CraftingMenuPortable;
import org.zeith.improvableskills.client.gui.abil.ench.ContainerEnchPowBook;
import org.zeith.improvableskills.client.gui.abil.ench.ContainerPortableEnchantment;

@SimplyRegister
public interface GuiHooksIS
{
	@RegistryName("enchantment")
	MenuType<ContainerPortableEnchantment> ENCHANTMENT = IMenuTypeExtension.create((windowId, inv, data) -> new ContainerPortableEnchantment(windowId, inv));
	
	@RegistryName("repair")
	MenuType<AnvilMenuPortable> REPAIR = IMenuTypeExtension.create((windowId, inv, data) -> new AnvilMenuPortable(windowId, inv));
	
	@RegistryName("enchantment_setup")
	MenuType<ContainerEnchPowBook> ENCH_POWER_BOOK_IO = IMenuTypeExtension.create((windowId, inv, data) -> new ContainerEnchPowBook(windowId, inv));
	
	@RegistryName("crafting")
	MenuType<CraftingMenuPortable> CRAFTING = IMenuTypeExtension.create((windowId, inv, data) -> new CraftingMenuPortable(windowId, inv, ContainerLevelAccess.create(inv.player.level(), inv.player.blockPosition())));
}