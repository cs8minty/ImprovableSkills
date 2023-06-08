package org.zeith.improvableskills.client.gui.abil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.client.gui.GuiCentered;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutButton;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutUV;
import org.zeith.improvableskills.net.PacketSetMagnetismData;

import java.awt.*;

public class GuiMagnetism
		extends GuiCentered
		implements IGuiSkillDataConsumer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/magnetism.png");
	
	protected PlayerSkillData data;
	
	public final UV main, slider;
	
	public GuiMagnetism(PlayerSkillData data)
	{
		this.data = data;
		if(data != null) sliderValue = data.magnetismRange / 8F;
		setSize(176, 85);
		this.main = new UV(TEXTURE, 0, 0, xSize, ySize);
		this.slider = new UV(TEXTURE, 176, 40, 4, 10);
	}
	
	protected float sliderValue;
	protected Rectangle sliderRect;
	protected boolean draggingSlider;
	
	protected GuiCustomButton toggleButton;
	
	@Override
	protected void init()
	{
		super.init();
		
		sliderRect = new Rectangle(guiLeft + 25, guiTop + 53, 126, 10);
		draggingSlider = false;
		
		toggleButton = addRenderableWidget(new GuiCustomButton(0, guiLeft + 25, guiTop + 25, 20, 20, Component.literal(""), btn ->
		{
			Network.sendToServer(new PacketSetMagnetismData(data.magnetism = !data.magnetism));
			new OTEFadeOutButton(btn, 15);
			for(int i = 0; i < 3; ++i)
				new OTEFadeOutUV(new UV(TEXTURE, 176, data != null && data.magnetism ? 20 : 0, 20, 20), 20, 20, btn.getX(), btn.getY(), 15 + i * 10);
		})
		{
			@Override
			protected void renderBg(GuiGraphics pose, Minecraft mc, int x, int y)
			{
				super.renderBg(pose, mc, x, y);
				FXUtils.bindTexture(TEXTURE);
				RenderUtils.drawTexturedModalRect(pose, this.getX(), this.getY(), 176, data != null && data.magnetism ? 0 : 20, 20, 20);
			}
		});
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int btn)
	{
		if(btn == 0 && sliderRect.contains(x, y))
		{
			draggingSlider = true;
			return false;
		}
		
		return super.mouseClicked(x, y, btn);
	}
	
	@Override
	public boolean mouseReleased(double x, double y, int btn)
	{
		if(draggingSlider && btn == 0)
		{
			float value = Mth.clamp((float) (x - 2 - sliderRect.x) / (sliderRect.width - 4), 0, 1) * 8F;
			Network.sendToServer(new PacketSetMagnetismData(value));
			if(data != null) data.magnetismRange = value;
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
			draggingSlider = false;
		}
		
		return super.mouseReleased(x, y, btn);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!draggingSlider && data != null)
			sliderValue = data.magnetismRange / 8F;
	}
	
	public Component getInformation(float range)
	{
		var magnetic = data != null && data.magnetism;
		var rangeS = "%.01f".formatted(range);
		return !magnetic
				? Component.translatable("text.improvableskills.magnetism.off")
				: Component.translatable("text.improvableskills.magnetism.on", Component.literal(rangeS));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics gfx, float partialTime, int mouseX, int mouseY)
	{
		var pose = gfx.pose();
		float value = data != null ? data.magnetismRange : 0;
		
		renderBackground(gfx);
		
		toggleButton.active = data != null;
		
		if(draggingSlider)
		{
			value = Mth.clamp((float) (mouseX - 2 - sliderRect.x) / (sliderRect.width - 4), 0, 1) * 8F;
			sliderValue = value / 8F;
		}
		
		main.render(pose, guiLeft, guiTop);
		slider.render(pose, guiLeft + 26 + 120 * sliderValue, guiTop + 53);
		
		pose.pushPose();
		
		float scale = 0.75F;
		
		pose.translate(guiLeft + 48, guiTop + 25.25F, 0);
		pose.scale(scale, scale, scale);
		int width = 102;
		width /= scale;
		int y = 0;
		for(var comp : font.split(getInformation(value), width))
		{
			gfx.drawString(font, comp, 0, y, 0xFFFFFF, true);
			y += 9;
		}
		pose.popPose();
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
}