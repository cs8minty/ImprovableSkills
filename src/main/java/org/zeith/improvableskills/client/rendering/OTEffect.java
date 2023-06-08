package org.zeith.improvableskills.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.utils.ScaledResolution;

@OnlyIn(Dist.CLIENT)
public class OTEffect
{
	protected Screen currentGui = Minecraft.getInstance().screen;
	protected int mouseX, mouseY;
	
	/**
	 * Should this effect render in gui?
	 */
	public boolean renderGui = true;
	
	/**
	 * Should this effect render in HUD?
	 */
	public boolean renderHud = true;
	
	public double x, y;
	public double prevX, prevY;
	public double width, height;
	
	public boolean expired = false;
	
	{
		width = Minecraft.getInstance().getWindow().getScreenWidth();
		height = Minecraft.getInstance().getWindow().getScreenHeight();
	}
	
	public void render(GuiGraphics gfx, float partialTime)
	{
	}
	
	public void update()
	{
		prevX = x;
		prevY = y;
	}
	
	public void setExpired()
	{
		this.expired = true;
	}
	
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		x = handleResizeXd(x, prev, nev);
		prevX = handleResizeXd(prevX, prev, nev);
		
		y = handleResizeYd(y, prev, nev);
		prevY = handleResizeYd(prevY, prev, nev);
		
		width = Minecraft.getInstance().getWindow().getScreenWidth();
		height = Minecraft.getInstance().getWindow().getScreenHeight();
	}
	
	public void setWhiteColor()
	{
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}
	
	protected static double handleResizeXd(double x, ScaledResolution prev, ScaledResolution nev)
	{
		return x / prev.getScaledWidth_double() * nev.getScaledWidth_double();
	}
	
	protected static double handleResizeYd(double y, ScaledResolution prev, ScaledResolution nev)
	{
		return y / prev.getScaledHeight_double() * nev.getScaledHeight_double();
	}
	
	protected static int handleResizeXi(int x, ScaledResolution prev, ScaledResolution nev)
	{
		return x / prev.getScaledWidth() * nev.getScaledWidth();
	}
	
	protected static int handleResizeYi(int y, ScaledResolution prev, ScaledResolution nev)
	{
		return y / prev.getScaledHeight() * nev.getScaledHeight();
	}
	
	protected static int[] handleResizeXiv(int[] x, ScaledResolution prev, ScaledResolution nev)
	{
		int[] v = x.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeXi(x[i], prev, nev);
		return v;
	}
	
	protected static int[] handleResizeYiv(int[] y, ScaledResolution prev, ScaledResolution nev)
	{
		int[] v = y.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeYi(y[i], prev, nev);
		return v;
	}
	
	protected static double[] handleResizeXdv(double[] x, ScaledResolution prev, ScaledResolution nev)
	{
		double[] v = x.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeXd(x[i], prev, nev);
		return v;
	}
	
	protected static double[] handleResizeYdv(double[] y, ScaledResolution prev, ScaledResolution nev)
	{
		double[] v = y.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeYd(y[i], prev, nev);
		return v;
	}
}