package org.zeith.improvableskills.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.utils.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public class OnTopEffects
		implements IGuiOverlay
{
	public static List<OTEffect> effects = new ArrayList<>();
	
	private ScaledResolution resolution;
	
	public OnTopEffects()
	{
		MinecraftForge.EVENT_BUS.addListener(this::tick);
		MinecraftForge.EVENT_BUS.addListener(this::renderInGui);
	}
	
	public void tick(TickEvent.ClientTickEvent e)
	{
		var mc = Minecraft.getInstance();
		
		SyncSkills.doCheck(mc.player);
		
		if(e.phase == TickEvent.Phase.END)
		{
			for(int i = 0; i < effects.size(); ++i)
			{
				OTEffect eff = effects.get(i);
				
				if(eff.expired)
				{
					effects.remove(i);
					continue;
				}
				
				eff.update();
			}
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getInstance());
			
			if(resolution == null)
				resolution = sr;
			
			if(sr.getScaledHeight() != resolution.getScaledHeight() || sr.getScaledWidth() != resolution.getScaledWidth())
			{
				for(int i = 0; i < effects.size(); ++i)
				{
					OTEffect eff = effects.get(i);
					eff.resize(resolution, sr);
				}
			}
			
			resolution = sr;
			
			IForgeRegistry<PageletBase> pagelets = ImprovableSkills.PAGELETS();
			
			for(var key : GuiTabbable.EXTENSIONS.keySet())
			{
				Tuple2.Mutable2<Float, Float> val = GuiTabbable.EXTENSIONS.get(key);
				
				Float target = val.a();
				Float current = val.b();
				
				float dif = Math.max(-.125F, Math.min(.125F, target - current));
				
				val.setB(current + dif);
				
				PageletBase base = pagelets.getValue(key);
				if(target < .5 && base != null && base.doesPop())
				{
					float v = (System.currentTimeMillis() + Math.abs(key.hashCode())) % 5000L / 5000F;
					
					if(current < v)
						val.setB(v);
				}
			}
		}
	}
	
	public void renderInGui(ScreenEvent.Render.Post e)
	{
		var gs = e.getScreen();
		int mx = e.getMouseX(), my = e.getMouseY();
		float pt = Minecraft.getInstance().getPartialTick();
		
		var pose = e.getPoseStack();
		
		pose.pushPose();
		pose.translate(0, 0, 300);
		for(int i = 0; i < effects.size(); ++i)
		{
			OTEffect eff = effects.get(i);
			
			if(eff.expired || !eff.renderGui)
				continue;
			
			eff.currentGui = gs;
			eff.mouseX = mx;
			eff.mouseY = my;
			
			RenderSystem.enableBlend();
			pose.pushPose();
			eff.render(pose, pt);
			pose.popPose();
		}
		pose.popPose();
	}
	
	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight)
	{
		float pt = partialTick;
		
		for(int i = 0; i < effects.size(); ++i)
		{
			OTEffect eff = effects.get(i);
			
			if(eff.expired || !eff.renderHud)
				continue;
			
			poseStack.pushPose();
			RenderSystem.enableBlend();
			eff.render(poseStack, pt);
			poseStack.popPose();
		}
	}
}