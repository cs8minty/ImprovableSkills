package org.zeith.improvableskills.client.rendering.ote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.rendering.OTEffect;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.init.ItemsIS;

public class OTEFadeOutButton
		extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private int totTime, prevTime, time;
	private Button uv;
	
	public OTEFadeOutButton(Button uv, int time)
	{
		renderHud = false;
		this.uv = uv;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
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
		
		Minecraft mc = Minecraft.getInstance();
		Font fontrenderer = mc.font;
		ResourceLocation rl = uv instanceof GuiCustomButton ? new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/icons.png") : new ResourceLocation("minecraft", "textures/gui/widgets.png");
		
		float scale = 1F + (float) Math.sqrt(t);
		int a = (int) ((1 - t / totTime) * .75F * 255);
		
		pose.pushPose();
		
		RenderSystem.enableBlend();
		
		{
			int i = !uv.active ? 0 : uv.isMouseOver(mouseX, mouseY) ? 2 : 1;
			
			RenderSystem.setShaderColor(1F, 1F, 1F, a / 255F);
			
			float yo = uv instanceof GuiCustomButton ? 0 : 46;
			
			new UV(rl, 0, yo + i * 20, uv.getWidth() / 2 - scale / 2, uv.getHeight()) //
					.render(pose, uv.getX() - scale / 2, uv.getY() - scale / 2, uv.getWidth() / 2 + scale / 2, uv.getHeight() + scale);
			
			new UV(rl, 200 - uv.getWidth() / 2 + scale / 2, yo + i * 20, uv.getWidth() / 2 - scale / 2, uv.getHeight()) //
					.render(pose, uv.getX() + uv.getWidth() / 2, uv.getY() - scale / 2, uv.getWidth() / 2 + scale / 2, uv.getHeight() + scale);
			
			int j = 14737632;
			
			int fg = uv.getFGColor();
			
			if(fg != 0) j = fg;
			else if(!uv.active) j = 10526880;
			else if(uv.isMouseOver(mouseX, mouseY)) j = 16777120;
			
			setWhiteColor();
			
			fontrenderer.drawShadow(pose, uv.getMessage(), uv.getX() + (uv.getWidth() - fontrenderer.width(uv.getMessage())) / 2, uv.getY() + (uv.getHeight() - fontrenderer.lineHeight) / 2 + 1, a << 24 | j);
		}
		
		setWhiteColor();
		pose.popPose();
	}
}