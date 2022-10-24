package org.zeith.improvableskills.custom.pagelets;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.GuiAbilityBook;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;

public class PageletAbilities
		extends PageletBase
{
	{
		setIcon(new ItemStack(Blocks.ENCHANTING_TABLE));
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":abilities"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return new GuiAbilityBook(this, data);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isVisible(PlayerSkillData data)
	{
		return data.getAbilityCount() > 0;
	}
}