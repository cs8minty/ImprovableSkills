package org.zeith.improvableskills.custom.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.SoundsIS;
import org.zeith.improvableskills.net.PacketOpenSkillsBook;

public class ItemSkillsBook
		extends Item
{
	public ItemSkillsBook(Properties props)
	{
		super(props);
	}
	
	public ItemSkillsBook()
	{
		this(new Properties().stacksTo(1));
		ImprovableSkills.TAB.add(this);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(!level.isClientSide() && player instanceof ServerPlayer mp)
			PacketOpenSkillsBook.sync(mp);
		else
			level.playSound(player, player.blockPosition(), SoundsIS.PAGE_TURNS, SoundSource.PLAYERS, 0.25F, 1F);
		return super.use(level, player, hand);
	}
	
	
	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(entityIn instanceof ServerPlayer mp && !worldIn.isClientSide)
			PlayerDataManager.handleDataSafely(mp, data -> data.hasCraftedSkillBook = true);
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}