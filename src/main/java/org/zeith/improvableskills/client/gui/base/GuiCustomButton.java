package org.zeith.improvableskills.client.gui.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.client.texture.HttpTextureDownloader;
import org.zeith.improvableskills.ImprovableSkills;

import java.util.function.Supplier;

public class GuiCustomButton
		extends Button
{
	protected static final ResourceLocation CBUTTON_TEXTURES = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/icons.png");
	
	public SoundEvent customClickSound;
	
	public final int id;
	
	public GuiCustomButton(int id, int x, int y, int widthIn, int heightIn, String buttonText, OnPress action)
	{
		this(id, x, y, widthIn, heightIn, Component.literal(buttonText), action);
	}
	
	public GuiCustomButton(int id, int x, int y, int widthIn, int heightIn, Component buttonText, OnPress action)
	{
		super(x, y, widthIn, heightIn, buttonText, action, Supplier::get);
		this.id = id;
	}
	
	private static final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/builtin/zeitheron.png");
	
	public static AbstractTexture getZeithAvatar()
	{
		return HttpTextureDownloader.create(texture, "https://h.zeith.org/discord/fetchAvatar/376091478142746625");
	}
	
	public GuiCustomButton setCustomClickSound(SoundEvent customClickSound)
	{
		this.customClickSound = customClickSound;
		return this;
	}
	
	protected void renderBg(GuiGraphics gfx, Minecraft minecraft, int mouseX, int mouseY)
	{
		int i = this.getTextureYCustom();
		gfx.blit(CBUTTON_TEXTURES, this.getX(), this.getY(), 0, i * 20, this.width / 2, this.height);
		gfx.blit(CBUTTON_TEXTURES, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, i * 20, this.width / 2, this.height);

//		gfx.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, 40 + i * 20);
	}
	
	@Override
	public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks)
	{
		if(this.visible)
		{
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			
			RenderSystem.setShaderTexture(0, CBUTTON_TEXTURES);
			gfx.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			
			this.renderBg(gfx, minecraft, mouseX, mouseY);
			
			int j = getFGColor();
			gfx.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
			gfx.setColor(1F, 1F, 1F, 1F);
		}
	}
	
	public int getTextureYCustom()
	{
		int i = 1;
		if(!this.active) i = 0;
		else if(this.isHoveredOrFocused()) i = 2;
		return i;
	}
	
	@Override
	public void playDownSound(SoundManager soundManager)
	{
		if(customClickSound == null)
			super.playDownSound(soundManager);
		else
			soundManager.play(SimpleSoundInstance.forUI(customClickSound, 1.0F));
	}
}