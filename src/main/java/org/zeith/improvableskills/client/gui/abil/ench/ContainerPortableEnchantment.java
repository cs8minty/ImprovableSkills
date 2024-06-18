package org.zeith.improvableskills.client.gui.abil.ench;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.GuiHooksIS;
import org.zeith.improvableskills.mixins.EnchantmentMenuAccessor;
import org.zeith.improvableskills.utils.GuiManager;

import java.util.List;

public class ContainerPortableEnchantment
		extends EnchantmentMenu
{
	public Level worldIn;
	public Player player;
	public int color;
	
	public ContainerPortableEnchantment(int windowId, Inventory inv)
	{
		super(windowId, inv, ContainerLevelAccess.create(inv.player.level(), inv.player.blockPosition()));
		player = inv.player;
		worldIn = inv.player.level();
	}
	
	@Override
	public MenuType<?> getType()
	{
		return GuiHooksIS.ENCHANTMENT;
	}
	
	@Override
	public boolean stillValid(Player playerIn)
	{
		return true;
	}
	
	@Override
	public void slotsChanged(Container p_39461_)
	{
		if(p_39461_ == this.enchantSlots)
		{
			ItemStack itemstack = p_39461_.getItem(0);
			if(!itemstack.isEmpty() && itemstack.isEnchantable())
			{
				this.access.execute((level, pos) ->
				{
					IdMap<Holder<Enchantment>> idmap = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
					float j = PlayerDataManager.handleDataSafely(player, data -> data.enchantPower, 0F).floatValue();
					
					this.random.setSeed(this.enchantmentSeed.get());
					
					for(int k = 0; k < 3; ++k)
					{
						this.costs[k] = EnchantmentHelper.getEnchantmentCost(this.random, k, (int) j, itemstack);
						this.enchantClue[k] = -1;
						this.levelClue[k] = -1;
						if(this.costs[k] < k + 1)
						{
							this.costs[k] = 0;
						}
						this.costs[k] = EventHooks.onEnchantmentLevelSet(worldIn, player.blockPosition(), k, (int) j, itemstack, costs[k]);
					}
					
					for(int l = 0; l < 3; ++l)
					{
						if(this.costs[l] > 0)
						{
							List<EnchantmentInstance> list = ((EnchantmentMenuAccessor) this).callGetEnchantmentList(level.registryAccess(), itemstack, l, this.costs[l]);
							if(!list.isEmpty())
							{
								EnchantmentInstance enchantmentinstance = list.get(this.random.nextInt(list.size()));
								this.enchantClue[l] = idmap.getId(enchantmentinstance.enchantment);
								this.levelClue[l] = enchantmentinstance.level;
							}
						}
					}
					
					this.broadcastChanges();
				});
			} else
			{
				for(int i = 0; i < 3; ++i)
				{
					this.costs[i] = 0;
					this.enchantClue[i] = -1;
					this.levelClue[i] = -1;
				}
			}
		}
	}
	
	int capturing;
	IntList capture = new IntArrayList();
	
	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if(capturing > 0)
		{
			capture.add(id);
			
			--capturing;
			
			if(capture.size() == 3 && capturing == 0)
				color = capture.getInt(0) << 16 | capture.getInt(1) << 8 | capture.getInt(2);
			
			return true;
		}
		
		if(id == 121)
		{
			capturing = 3;
			return true;
		}
		
		if(id == 122)
		{
			if(!worldIn.isClientSide)
				GuiManager.openGuiCallback(GuiHooksIS.ENCH_POWER_BOOK_IO, player);
			return true;
		}
		
		if(id >= 0 && id < this.costs.length)
		{
			ItemStack itemstack = this.enchantSlots.getItem(0);
			ItemStack itemstack1 = this.enchantSlots.getItem(1);
			int i = id + 1;
			if((itemstack1.isEmpty() || itemstack1.getCount() < i) && !player.getAbilities().instabuild)
			{
				return false;
			} else if(this.costs[id] <= 0 || itemstack.isEmpty() || (player.experienceLevel < i || player.experienceLevel < this.costs[id]) && !player.getAbilities().instabuild)
			{
				return false;
			} else
			{
				this.access.execute((level, pos) ->
				{
					ItemStack itemstack2 = itemstack;
					List<EnchantmentInstance> list = ((EnchantmentMenuAccessor) this).callGetEnchantmentList(level.registryAccess(), itemstack, id, this.costs[id]);
					if(list.isEmpty()) return;
					
					player.onEnchantmentPerformed(itemstack, i);
					boolean flag = itemstack.is(Items.BOOK);
					if(flag)
					{
						itemstack2 = itemstack.transmuteCopy(Items.ENCHANTED_BOOK);
						this.enchantSlots.setItem(0, itemstack2);
					}
					
					for(var enchantmentinstance : list)
					{
						itemstack2.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
					}
					
					itemstack1.consume(i, player);
					if(itemstack1.isEmpty())
					{
						this.enchantSlots.setItem(1, ItemStack.EMPTY);
					}
					
					player.awardStat(Stats.ENCHANT_ITEM);
					if(player instanceof ServerPlayer)
					{
						CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, itemstack2, i);
					}
					
					this.enchantSlots.setChanged();
					this.enchantmentSeed.set(player.getEnchantmentSeed());
					this.slotsChanged(this.enchantSlots);
					level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
				});
				return true;
			}
		} else
		{
			Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + id);
			return false;
		}
	}
}