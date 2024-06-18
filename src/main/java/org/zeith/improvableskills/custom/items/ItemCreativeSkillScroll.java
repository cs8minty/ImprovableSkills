package org.zeith.improvableskills.custom.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.net.PacketScrollUnlockedSkill;

import java.util.ArrayList;
import java.util.List;

public class ItemCreativeSkillScroll
		extends Item
{
	public ItemCreativeSkillScroll()
	{
		super(new Properties().stacksTo(1));
		ImprovableSkills.TAB.add(this);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.translatable(getDescriptionId() + ".tooltip0").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(getDescriptionId() + ".tooltip1").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
	{
		var held = playerIn.getItemInHand(handIn);
		
		if(worldIn.isClientSide) return new InteractionResultHolder<>(InteractionResult.PASS, held);
		
		return PlayerDataManager.handleDataSafely(playerIn, data ->
		{
			var bases = ImprovableSkills.SKILLS;
			
			List<PlayerSkillBase> given = new ArrayList<>();
			
			for(var base : bases)
			{
				if(!data.hasSkillScroll(base) && base.getScrollState().hasScroll())
				{
					data.unlockSkillScroll(base, false);
					given.add(base);
				} else if(data.getSkillLevel(base) < base.getMaxLevel())
				{
					data.setSkillLevel(base, data.getSkillLevel(base) + 1);
					given.add(base);
				}
			}
			
			if(!given.isEmpty())
			{
				ItemStack used = held.copy();
				if(!playerIn.isCreative())
					held.shrink(1);
				
				playerIn.swing(handIn);
				worldIn.playSound(null, playerIn.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 1F);
				
				int slot = handIn == InteractionHand.OFF_HAND ? -2 : playerIn.getInventory().selected;
				Network.sendTo(new PacketScrollUnlockedSkill(slot, used, given.stream().map(PlayerSkillBase::getRegistryName).toArray(ResourceLocation[]::new)), playerIn);
			}
			
			return new InteractionResultHolder<>(!given.isEmpty() ? InteractionResult.SUCCESS : InteractionResult.PASS, held);
		}, new InteractionResultHolder<>(InteractionResult.PASS, held));
	}
}