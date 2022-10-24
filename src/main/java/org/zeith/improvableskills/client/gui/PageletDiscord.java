package org.zeith.improvableskills.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;

public class PageletDiscord
		extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/discord.png");
	
	{
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":discord1"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return new GuiDiscord(this);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Object getIcon()
	{
		Object o = super.getIcon();
		if(!(o instanceof AbstractTexture))
			setIcon(o = Minecraft.getInstance().getTextureManager().getTexture(texture));
		return o;
	}
	
	@Override
	public boolean isRight()
	{
		return false;
	}
}