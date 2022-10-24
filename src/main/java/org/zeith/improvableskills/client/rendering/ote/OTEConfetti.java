package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.util.colors.ColorHelper;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.client.rendering.OnTopEffects;

import java.util.Random;

public class OTEConfetti
		extends OTEffect
{
	public static final Random random = new Random();
	
	public int color = 255 << 24 | ColorHelper.packRGB(Math.max(.5F, random.nextFloat()), Math.max(.5F, random.nextFloat()), Math.max(.5F, random.nextFloat()));
	
	public int ticksExisted;
	
	public float motionX, motionY;
	
	/**
	 * Converts a [0-1] value to another [0-1] value, but using sine function
	 */
	public static float sineF(float val)
	{
		return (float) Math.sin(Math.toRadians(val * 90F));
	}
	
	public static ItemStack getSkull(String player)
	{
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
		stack.addTagElement("SkullOwner", StringTag.valueOf(player));
		return stack;
	}
	
	public OTEConfetti(double x, double y)
	{
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		renderHud = false;
		
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		ticksExisted++;
		
		x += motionX;
		y += motionY;
		
		motionY += .05;
		
		motionX *= .98535735;
		motionY *= .98535735;
		
		int ma = 160 - (Math.abs(hashCode()) % 40);
		
		if(ticksExisted >= ma || y < -8 || x < -8 || y > height || x > width)
		{
			setExpired();
		}
	}
	
	@Override
	public void render(PoseStack pose, float partialTime)
	{
		float alpha = sineF((40F - ticksExisted - partialTime) / 40F);
		int ma = 160 - (Math.abs(hashCode()) % 40);
		
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = ticksExisted + partialTime;
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		FXUtils.bindTexture(ImprovableSkills.MOD_ID, "textures/particles/sparkle.png");
		
		int tx = 64 * (int) (ticksExisted / (float) ma * 3F);
		
		RenderSystem.enableBlend();
		
		float scale = 1 / 8F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= ma - 5)
			scale *= 1 - (t - ma + 5) / 5F;
		
		RenderSystem.setShaderColor(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), .9F * ColorHelper.getAlpha(color));
		
		for(int i = 0; i < 3; ++i)
		{
			float ps = i == 0 ? scale : i == 2 ? (float) ((Math.sin(hashCode() % 90 + t / 2) + 1) / 2.5 * scale) : scale / 2;
			
			pose.pushPose();
			RenderSystem.blendFunc(770, i == 0 ? 771 : 772);
			pose.translate(cx - 64 * ps / 2, cy - 64 * ps / 2, 0);
			pose.scale(ps, ps, ps);
			RenderUtils.drawTexturedModalRect(pose, 0, 0, tx, 0, 64, 64);
			pose.popPose();
		}
		
		RenderSystem.defaultBlendFunc();
		setWhiteColor();
	}
}