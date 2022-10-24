package org.zeith.improvableskills.client.gui.abil.ench;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.GuiHooksIS;
import org.zeith.improvableskills.utils.GuiManager;

public class ContainerEnchPowBook
		extends AbstractContainerMenu
{
	public final SimpleInventory inventory = new SimpleInventory(1);
	
	public ContainerEnchPowBook(int windowId, Inventory playerInv)
	{
		super(GuiHooksIS.ENCH_POWER_BOOK_IO, windowId);
		
		for(int i = 0; i < 3; ++i)
		{
			for(int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
			}
		}
		
		for(int k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(playerInv, k, 8 + k * 18, 140));
		}
		
		addSlot(new Slot(inventory, 0, 176 / 2 - 36, 32)
		{
			@Override
			public boolean mayPlace(ItemStack stack)
			{
				return !stack.isEmpty() && stack.getItem() == Items.BOOK;
			}
		});
	}
	
	@Override
	public boolean clickMenuButton(Player playerIn, int id)
	{
		if(id == 11)
		{
			if(!playerIn.level.isClientSide)
				PlayerDataManager.handleDataSafely(playerIn, data ->
				{
					ItemStack item = inventory.getStackInSlot(0);
					if((item.isEmpty() || (item.getItem() == Items.BOOK && item.getCount() < 64)) && data.enchantPower > 0F)
					{
						if(item.isEmpty())
							inventory.setItem(0, new ItemStack(Items.BOOK));
						else
							item.grow(1);
						data.enchantPower -= 1F;
						data.sync();
						playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, playerIn.level.random.nextFloat() * 0.1F + 1.5F);
					}
				});
			return true;
		}
		if(id == 0)
		{
			if(!playerIn.level.isClientSide)
				PlayerDataManager.handleDataSafely(playerIn, data ->
				{
					ItemStack item = inventory.getStackInSlot(0);
					if(!item.isEmpty() && item.getItem() == Items.BOOK && data.enchantPower < 15F)
					{
						item.shrink(1);
						data.enchantPower += 1F;
						data.sync();
						playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, playerIn.level.random.nextFloat() * 0.1F + 1.5F);
					}
				});
			return true;
		}
		
		if(id == 1)
		{
			GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, playerIn);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slotId)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotId);
		if(slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if(slot.container == player.getInventory())
			{
				if(!this.moveItemStackTo(itemstack1, 36, slots.size(), true))
					return ItemStack.EMPTY;
			} else
			{
				if(!this.moveItemStackTo(itemstack1, 0, 36, true))
					return ItemStack.EMPTY;
			}
			
			if(itemstack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			} else
			{
				slot.setChanged();
			}
			
			if(itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
	
	@Override
	public void removed(Player playerIn)
	{
		clearContainer(playerIn, inventory);
		super.removed(playerIn);
		
		// Magic
		if(playerIn.getServer() != null)
			Threading.createAndStart(() ->
					playerIn.getServer()
							.execute(() ->
									GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, playerIn)
							)
			);
	}
}