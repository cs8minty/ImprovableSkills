package org.zeith.improvableskills.client.rendering.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer ir, int l)
	{
		tooltip.skill().tex.toUV(false).render(pose, x, y - 1, 24, 24);
	}
}
