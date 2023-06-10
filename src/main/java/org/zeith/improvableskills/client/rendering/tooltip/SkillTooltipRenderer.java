package org.zeith.improvableskills.client.rendering.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.zeith.improvableskills.api.client.IClientSkillExtensions;
import org.zeith.improvableskills.api.tooltip.SkillTooltip;

public class SkillTooltipRenderer
		implements ClientTooltipComponent
{
	private final SkillTooltip tooltip;
	
	public SkillTooltipRenderer(SkillTooltip tooltip)
	{
		this.tooltip = tooltip;
	}
	
	@Override
	public int getHeight()
	{
		return 24;
	}
	
	@Override
	public int getWidth(Font font)
	{
		return 24;
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics gfx)
	{
		if(IClientSkillExtensions.of(tooltip.skill()).slotRenderer().drawSlot(gfx, x, y - 1, 24, 24, 0, 1F))
			return;
		
		tooltip.skill().tex.toUV(false).render(gfx.pose(), x, y - 1, 24, 24);
	}
}
