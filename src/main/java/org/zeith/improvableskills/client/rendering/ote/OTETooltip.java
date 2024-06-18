package org.zeith.improvableskills.client.rendering.ote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.client.rendering.OnTopEffects;

import java.util.*;

public class OTETooltip
		extends OTEffect
{
	public final List<Component> tooltip = new ArrayList<>();
	
	private int time;
	private static OTETooltip cinst;
	
	public static void showTooltip(Component... tip)
	{
		showTooltip(Arrays.asList(tip));
	}
	
	public static void showTooltip(List<Component> tip)
	{
		if(cinst == null) cinst = new OTETooltip();
		cinst.tooltip.clear();
		cinst.tooltip.addAll(tip);
		cinst.time = 0;
		
		/* Always on top */
		if(OnTopEffects.effects.indexOf(cinst) != OnTopEffects.effects.size() - 1)
		{
			OnTopEffects.effects.remove(cinst);
			OnTopEffects.effects.addLast(cinst);
		}
	}
	
	{
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		if(time++ >= 8)
			setExpired();
		else
		{
			cinst = this;
			
			/* Always on top */
			if(OnTopEffects.effects.indexOf(this) != OnTopEffects.effects.size() - 1)
			{
				OnTopEffects.effects.remove(this);
				OnTopEffects.effects.addLast(this);
			}
		}
	}
	
	@Override
	public void setExpired()
	{
		super.setExpired();
		cinst = null;
	}
	
	@Override
	public void render(GuiGraphics gfx, float partialTime)
	{
		var pose = gfx.pose();
		if(!tooltip.isEmpty())
		{
			pose.pushPose();
			pose.translate(0, 0, 200);
			gfx.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY);
			pose.popPose();
			tooltip.clear();
		}
	}
}