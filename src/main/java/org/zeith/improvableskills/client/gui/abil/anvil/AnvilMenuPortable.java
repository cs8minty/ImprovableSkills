package org.zeith.improvableskills.client.gui.abil.anvil;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.zeith.improvableskills.init.GuiHooksIS;

public class AnvilMenuPortable
		extends AnvilMenu
{
	public AnvilMenuPortable(int windowId, Inventory inventory)
	{
		super(windowId, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
	}
	
	@Override
	public MenuType<?> getType()
	{
		return GuiHooksIS.REPAIR;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
