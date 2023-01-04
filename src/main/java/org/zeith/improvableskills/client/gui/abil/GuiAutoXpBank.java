package org.zeith.improvableskills.client.gui.abil;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.client.gui.GuiCentered;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutButton;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutUV;
import org.zeith.improvableskills.net.PacketSetAutoXpBankData;

public class GuiAutoXpBank
		extends GuiCentered
		implements IGuiSkillDataConsumer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/auto_xp_bank.png");
	
	protected PlayerSkillData data;
	
	public final UV main;
	
	public GuiAutoXpBank(PlayerSkillData data)
	{
		this.data = data;
		setSize(176, 85);
		this.main = new UV(TEXTURE, 0, 0, xSize, ySize);
	}
	
	protected GuiCustomButton toggleButton;
	protected EditBox keepLevels;
	
	@SuppressWarnings("ConstantConditions")
	@Override
	protected void init()
	{
		super.init();
		
		keepLevels = addRenderableWidget(new EditBox(font, guiLeft + 67, guiTop + 58, 43, font.lineHeight, keepLevels, Component.empty()));
		keepLevels.setResponder(str ->
		{
			try
			{
				keepLevels.setTextColor(ChatFormatting.WHITE.getColor());
				float levels = Float.parseFloat(str.replace(',', '.'));
				if(levels < 0) throw new IllegalArgumentException();
				int xpt = XPUtil.getXPTotal((int) levels, levels % 1F);
				if(data.autoXpBankThreshold != xpt)
					Network.sendToServer(new PacketSetAutoXpBankData(data.autoXpBankThreshold = xpt));
			} catch(Throwable ignored)
			{
				keepLevels.setTextColor(ChatFormatting.RED.getColor());
			}
		});
		keepLevels.setValue(
				"%.03f".formatted(XPUtil.getLevelFromXPValue(data.autoXpBankThreshold) + XPUtil.getCurrentFromXPValue(data.autoXpBankThreshold))
						.replace(',', '.')
		);
		keepLevels.setBordered(false);
		
		toggleButton = addRenderableWidget(new GuiCustomButton(0, guiLeft + 25, guiTop + 25, 20, 20, net.minecraft.network.chat.Component.literal(""), btn ->
		{
			Network.sendToServer(new PacketSetAutoXpBankData(data.autoXpBank = !data.autoXpBank));
			new OTEFadeOutButton(btn, 15);
			for(int i = 0; i < 3; ++i)
				new OTEFadeOutUV(new UV(TEXTURE, 176, data != null && data.autoXpBank ? 20 : 0, 20, 20), 20, 20, btn.getX(), btn.getY(), 15 + i * 10);
		})
		{
			@Override
			protected void renderBg(PoseStack pose, Minecraft mc, int x, int y)
			{
				FXUtils.bindTexture(TEXTURE);
				RenderUtils.drawTexturedModalRect(pose, this.getX(), this.getY(), 176, data != null && data.autoXpBank ? 0 : 20, 20, 20);
			}
		});
	}
	
	public net.minecraft.network.chat.Component getInformation(float range)
	{
		var magnetic = data != null && data.autoXpBank;
		var levels = "%.01f".formatted(range);
		return !magnetic
				? net.minecraft.network.chat.Component.translatable("text.improvableskills.auto_xp_bank.off")
				: net.minecraft.network.chat.Component.translatable("text.improvableskills.auto_xp_bank.on", Component.literal(levels));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		float value = data != null ? XPUtil.getLevelFromXPValue(data.autoXpBankThreshold) + XPUtil.getCurrentFromXPValue(data.autoXpBankThreshold) : 0;
		
		renderBackground(pose);
		
		toggleButton.active = data != null;
		
		main.render(pose, guiLeft, guiTop);
		
		pose.pushPose();
		
		float scale = 0.75F;
		
		pose.translate(guiLeft + 48, guiTop + 25.25F, 0);
		pose.scale(scale, scale, scale);
		int width = 102;
		width /= scale;
		int y = 0;
		for(var comp : font.split(getInformation(value), width))
		{
			font.drawShadow(pose, comp, 0, y, 0xFFFFFF);
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