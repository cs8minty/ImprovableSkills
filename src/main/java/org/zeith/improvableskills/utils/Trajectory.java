package org.zeith.improvableskills.utils;

import net.minecraft.util.Mth;

public class Trajectory
{
	public static double[][] makeBroken2DTrajectory(double x, double y, double tx, double ty, int coords, float timeOffset)
	{
		return makeBroken2DTrajectory(x, y, tx, ty, coords, timeOffset, 5F);
	}
	
	public static double[][] makeBroken2DTrajectory(double x, double y, double tx, double ty, int coords, float timeOffset, float offset)
	{
		double hDel = x - tx;
		double vDel = y - ty;
		
		float dx = (float) (hDel / coords);
		float dy = (float) (vDel / coords);
		
		boolean hBoost = Math.abs(hDel) > Math.abs(vDel);
		
		if(hBoost)
			dx *= 2.0F;
		else
			dy *= 2.0F;
		
		double[] xPoints = new double[coords + 1];
		double[] yPoints = new double[coords + 1];
		
		for(int a = 0; a <= coords; ++a)
		{
			float mx, my, phase = (float) a / (float) coords;
			
			mx = Mth.sin((timeOffset + a) / 7.0F) * offset * (1.0F - phase);
			my = Mth.sin((timeOffset + a) / 5.0F) * offset * (1.0F - phase);
			
			xPoints[a] = x - dx * a + mx;
			yPoints[a] = y - dy * a + my;
			
			if(hBoost)
				dx *= 1.0F - 1.0F / (coords * 3.0F / 2.0F);
			else
				dy *= 1.0F - 1.0F / (coords * 3.0F / 2.0F);
		}
		
		return new double[][] {
				xPoints,
				yPoints
		};
	}
}