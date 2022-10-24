package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.utils.ScaledResolution;
import org.zeith.improvableskills.utils.Trajectory;

public class OTESkillSparkle
		extends OTEffect
{
	private int color;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTESkillSparkle(double x, double y, double tx, double ty, int time, int color)
	{
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.color = color;
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
	public void render(PoseStack pose, float partialTime)
	{
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		FXUtils.bindTexture(ImprovableSkills.MOD_ID, "textures/particles/sparkle.png");
		
		int tx = 64 * (int) (time / (float) totTime * 3F);
		
		float scale = 1 / 8F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		RenderSystem.setShaderColor(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), 1);
		
		for(int i = 0; i < 3; ++i)
		{
			float ps = i == 0 ? scale : i == 2 ? (float) ((Math.sin(hashCode() % 90 + t / 2) + 1) / 2.5 * scale) : scale / 2;
			
			RenderSystem.blendFunc(770, i == 0 ? 771 : 772);
			
			pose.pushPose();
			pose.translate(cx - 64 * ps / 2, cy - 64 * ps / 2, 0);
			pose.scale(ps, ps, ps);
			RenderUtils.drawTexturedModalRect(pose, 0, 0, tx, 0, 64, 64);
			pose.popPose();
		}
		
		RenderSystem.blendFunc(770, 771);
		setWhiteColor();
	}
}