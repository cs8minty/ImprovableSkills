package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiCentered
		extends Screen
{
	protected int xSize = 176;
	protected int ySize = 166;
	protected int guiLeft;
	protected int guiTop;
	
	protected GuiCentered()
	{
		this(Component.literal(""));
	}
	
	protected GuiCentered(Component label)
	{
		super(label);
	}
	
	protected void setSize(int xSize, int ySize)
	{
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}
	
	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks)
	{
		drawGuiContainerBackgroundLayer(gfx, partialTicks, mouseX, mouseY);
		var pose = gfx.pose();
		pose.pushPose();
		pose.translate(0, 0, 100);
		super.render(gfx, mouseX, mouseY, partialTicks);
		pose.popPose();
	}
	
	protected void drawGuiContainerBackgroundLayer(GuiGraphics pose, float partialTime, int mouseX, int mouseY)
	{
	}
	
	@Override
	public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_)
	{
		InputConstants.Key mouseKey = InputConstants.getKey(p_97765_, p_97766_);
		if(super.keyPressed(p_97765_, p_97766_, p_97767_))
		{
			return true;
		} else if(this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey))
		{
			this.onClose();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
}