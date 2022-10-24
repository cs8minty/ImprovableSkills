package org.zeith.improvableskills.client.gui.abil.crafting;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.zeith.improvableskills.init.GuiHooksIS;

public class CraftingMenuPortable
		extends CraftingMenu
{
	public CraftingMenuPortable(int p_39353_, Inventory p_39354_)
	{
		super(p_39353_, p_39354_);
	}
	
	public CraftingMenuPortable(int p_39356_, Inventory p_39357_, ContainerLevelAccess p_39358_)
	{
		super(p_39356_, p_39357_, p_39358_);
	}
	
	@Override
	public MenuType<?> getType()
	{
		return GuiHooksIS.CRAFTING;
	}
	
	@Override
	public boolean stillValid(Player p_39368_)
	{
		return true;
	}
}