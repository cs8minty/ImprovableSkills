package org.zeith.improvableskills.client.gui.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.client.texture.HttpTextureDownloader;
import org.zeith.improvableskills.ImprovableSkills;

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
		super(x, y, widthIn, heightIn, buttonText, action);
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
	
	@Override
	public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		if(this.visible)
		{
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, CBUTTON_TEXTURES);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			int i = this.getYImage(this.isHoveredOrFocused());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.blit(pose, this.x, this.y, 0, i * 20, this.width / 2, this.height);
			this.blit(pose, this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);
			this.renderBg(pose, minecraft, mouseX, mouseY);
			int j = getFGColor();
			drawCenteredString(pose, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
		}
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