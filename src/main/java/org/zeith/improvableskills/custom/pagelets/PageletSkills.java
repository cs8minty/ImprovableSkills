package org.zeith.improvableskills.custom.pagelets;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.GuiSkillsBook;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.SkillsIS;

import java.util.function.Supplier;

public class PageletSkills
		extends PageletBase
{
	{
		setIcon((Supplier<ItemStack>) () -> ItemSkillScroll.of(SkillsIS.SILENT_FOOT));
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":skills"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return new GuiSkillsBook(this, data);
	}
}