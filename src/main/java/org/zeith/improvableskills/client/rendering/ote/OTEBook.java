package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.utils.ScaledResolution;

public class OTEBook
		extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private double tx, ty;
	private int totTime, prevTime, time;
	
	private static OTEBook book;
	
	public static void show(int time)
	{
		if(time == 0)
		{
			if(book != null && !book.expired)
				book.totTime = time + 8;
			return;
		}
		
		if(book != null && !book.expired)
		{
			book.totTime = Math.max(book.totTime, time);
			book.time = Math.min(5, book.time);
			book.prevTime = book.time;
		} else
		{
			Minecraft mc = Minecraft.getInstance();
			
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			int w = scaledresolution.getScaledWidth();
			int h = scaledresolution.getScaledHeight();
			
			new OTEBook(w - 12, h - 12, time);
		}
	}
	
	public OTEBook(double x, double y, int time)
	{
		renderGui = false;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		super.resize(prev, nev);
		tx = handleResizeXd(tx, prev, nev);
		ty = handleResizeYd(ty, prev, nev);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		time++;
		
		if(time >= totTime)
		{
			setExpired();
			book = null;
		} else
			book = this;
	}
	
	@Override
	public void render(GuiGraphics gfx, float partialTime)
	{
		var pose = gfx.pose();
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		
		Lighting.setupForFlatItems();
		
		float scale = 1F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		setWhiteColor();
		pose.pushPose();
		pose.translate(cx - 16 * scale / 2, cy - 16 * scale / 2, 0);
		pose.scale(scale, scale, scale);
		RenderUtils.renderItemIntoGui(pose, item, 0, 0);
		pose.popPose();
		setWhiteColor();
	}
}