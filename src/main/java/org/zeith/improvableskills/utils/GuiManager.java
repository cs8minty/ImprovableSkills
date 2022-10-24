package org.zeith.improvableskills.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class GuiManager
{
	public static void openGuiCallback(MenuType<?> menu, Player playerIn)
	{
		openGuiCallback(menu, playerIn, Component.literal(""));
	}
	
	public static void openGuiCallback(MenuType<?> menu, Player playerIn, Component label)
	{
		playerIn.openMenu(new SimpleMenuProvider((windowId, inv, player) -> menu.create(windowId, inv), label));
	}
}