package org.zeith.improvableskills.api.client;

import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface ISlotRenderer
{
	ISlotRenderer NONE = (gfx, x, y, width, height, hoverProgress, partialTicks) -> false;
	
	boolean drawSlot(GuiGraphics gfx, float x, float y, float width, float height, float hoverProgress, float partialTicks);
}