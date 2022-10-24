package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.init.ItemsIS;

public class OTEFadeOutUV
		extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private float w, h;
	private int totTime, prevTime, time;
	private UV uv;
	
	public OTEFadeOutUV(UV uv, float w, float h, double x, double y, int time)
	{
		renderHud = false;
		this.uv = uv;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.w = w;
		this.h = h;
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
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
		
		RenderSystem.enableBlend();
		Lighting.setupForFlatItems();
		
		float scale = 1F + (float) Math.sqrt(t);
		
		RenderSystem.blendFunc(770, 1);
		RenderSystem.setShaderColor(1, 1, 1, (1 - t / totTime) * .75F);
		uv.render(pose, x - scale / 2, y - scale / 2, w + scale, h + scale);
		setWhiteColor();
		RenderSystem.blendFunc(770, 771);
	}
}