package org.zeith.improvableskills.client.rendering.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.zeith.improvableskills.api.client.IClientAbilityExtensions;
import org.zeith.improvableskills.api.tooltip.AbilityTooltip;

public class AbilityTooltipRenderer
		implements ClientTooltipComponent
{
	private final AbilityTooltip tooltip;
	
	public AbilityTooltipRenderer(AbilityTooltip tooltip)
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
		if(IClientAbilityExtensions.of(tooltip.ability()).slotRenderer().drawSlot(gfx, x, y - 1, 24, 24, 1F, 1F))
			return;
		
		var tx = tooltip.ability().tex;
		tx.toUV(false).render(gfx.pose(), x, y - 1, 24, 24);
		tx.toUV(true).render(gfx.pose(), x, y - 1, 24, 24);
	}
}
