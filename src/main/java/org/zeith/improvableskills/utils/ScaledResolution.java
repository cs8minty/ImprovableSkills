package org.zeith.improvableskills.utils;

import net.minecraft.client.Minecraft;

public class ScaledResolution
{
	int scaledWidth, scaledHeight;
	double scaleFactor;
	
	public ScaledResolution(Minecraft minecraft)
	{
		var wnd = minecraft.getWindow();
		this.scaledWidth = wnd.getGuiScaledWidth();
		this.scaledHeight = wnd.getGuiScaledHeight();
		this.scaleFactor = wnd.getGuiScale();
	}
	
	public int getScaledWidth()
	{
		return scaledWidth;
	}
	
	public int getScaledHeight()
	{
		return scaledHeight;
	}
	
	public double getScaledWidth_double()
	{
		return scaledWidth;
	}
	
	public double getScaledHeight_double()
	{
		return scaledHeight;
	}
	
	public double getScaleFactor()
	{
		return scaleFactor;
	}
}