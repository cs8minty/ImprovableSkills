package org.zeith.improvableskills.client.rendering.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer ir, int l)
	{
		var tx = tooltip.ability().tex;
		tx.toUV(false).render(pose, x, y - 1, 24, 24);
		tx.toUV(true).render(pose, x, y - 1, 24, 24);
	}
}
