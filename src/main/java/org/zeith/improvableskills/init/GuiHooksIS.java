package org.zeith.improvableskills.init;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.client.gui.abil.anvil.AnvilMenuPortable;
import org.zeith.improvableskills.client.gui.abil.crafting.CraftingMenuPortable;
import org.zeith.improvableskills.client.gui.abil.ench.ContainerEnchPowBook;
import org.zeith.improvableskills.client.gui.abil.ench.ContainerPortableEnchantment;

@SimplyRegister
public class GuiHooksIS
{
	@RegistryName("enchantment")
	public static final MenuType<ContainerPortableEnchantment> ENCHANTMENT = IForgeMenuType.create((windowId, inv, data) -> new ContainerPortableEnchantment(windowId, inv));
	
	@RegistryName("repair")
	public static final MenuType<AnvilMenuPortable> REPAIR = IForgeMenuType.create((windowId, inv, data) -> new AnvilMenuPortable(windowId, inv));
	
	@RegistryName("enchantment_setup")
	public static final MenuType<ContainerEnchPowBook> ENCH_POWER_BOOK_IO = IForgeMenuType.create((windowId, inv, data) -> new ContainerEnchPowBook(windowId, inv));
	
	@RegistryName("crafting")
	public static final MenuType<CraftingMenuPortable> CRAFTING = IForgeMenuType.create((windowId, inv, data) -> new CraftingMenuPortable(windowId, inv, ContainerLevelAccess.create(inv.player.level(), inv.player.blockPosition())));
}