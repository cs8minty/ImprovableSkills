package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.improvableskills.client.gui.GuiXPBank;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.utils.ScaledResolution;
import org.zeith.improvableskills.utils.Trajectory;

public class OTEXpOrb
		extends OTEffect
{
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTEXpOrb(double x, double y, double tx, double ty, int time)
	{
		renderHud = false;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, Math.abs(hashCode() / 25F));
		xPoints = path[0];
		yPoints = path[1];
	}
	
	@Override
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		super.resize(prev, nev);
		tx = handleResizeXd(tx, prev, nev);
		ty = handleResizeYd(ty, prev, nev);
		xPoints = handleResizeXdv(xPoints, prev, nev);
		yPoints = handleResizeYdv(yPoints, prev, nev);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		int tt = xPoints.length;
		
		int cframe = (int) Math.round(time / (float) totTime * tt);
		
		x = xPoints[cframe];
		y = yPoints[cframe];
		
		time++;
		
		if(time >= totTime)
			setExpired();
	}
	
	@Override
	public void render(GuiGraphics gfx, float partialTime)
	{
		var pose = gfx.pose();
		if(!(currentGui instanceof GuiXPBank))
			return;
		
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		float r = (float) ((System.currentTimeMillis() + Math.abs(hashCode())) % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		FXUtils.bindTexture("minecraft", "textures/entity/experience_orb.png");
		
		int tx = 64 * (hashCode() % 3);
		
		float scale = 1 / 8F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		RenderSystem.setShaderColor(r, 1, 0, 1);
		
		pose.pushPose();
		pose.translate(cx - 64 * scale / 2, cy - 64 * scale / 2, 0);
		pose.scale(scale, scale, scale);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, tx, 0, 64, 64);
		pose.popPose();
		setWhiteColor();
	}
}