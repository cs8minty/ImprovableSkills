package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.vertex.PoseStack;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.utils.ScaledResolution;
import org.zeith.improvableskills.utils.Trajectory;

import java.util.Random;

public class OTESkill
		extends OTEffect
{
	public PlayerSkillBase item;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTESkill(double x, double y, double tx, double ty, int time, PlayerSkillBase item)
	{
		renderGui = false;
		this.totTime = time + 5;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.item = item;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, new Random().nextFloat() * 1000F, 5F);
		xPoints = path[0];
		yPoints = path[1];
		x = xPoints[0];
		y = yPoints[0];
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
		
		if(time > 5)
		{
			int cframe = (int) Math.round((time - 5) / (float) (totTime - 5) * tt);
			x = xPoints[cframe];
			y = yPoints[cframe];
		}
		
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
		
		float scale = 1F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		scale *= 16;
		
		setWhiteColor();
		item.tex.toUV(false).render(pose, cx - scale / 2, cy - scale / 2, scale, scale);
	}
}