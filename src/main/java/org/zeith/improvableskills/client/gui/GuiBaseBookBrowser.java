package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.*;
import org.zeith.improvableskills.init.SoundsIS;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public abstract class GuiBaseBookBrowser<TX extends GuiBaseBookBrowser.ITxInstance, P extends PageletBase>
		extends GuiTabbable<P>
		implements IGuiSkillDataConsumer
{
	public static final ResourceLocation PAPER_TEXTURE = ImprovableSkills.id("textures/gui/skills_gui_paper.png");
	
	protected final Rectangle scissorRect = new Rectangle();
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<TX, Tuple2.Mutable2<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int currentHover;
	
	protected final List<TX> texes = new ArrayList<>();
	
	protected PlayerSkillData data;
	
	public interface ITxInstance
	{
		UV toUV(boolean hover);
		
		List<Component> getHoverTooltip();
		
		default int[] getAllColors()
		{
			return TexturePixelGetter.getAllColors(toUV(true).path);
		}
		
		void renderDecorations(GuiGraphics gfx, float hoverProgress, double x, double y, float partialTicks);
		
		ClickFeedback onMouseClicked(int button);
	}
	
	public record ClickFeedback(boolean playSound, boolean spawnFadeout, boolean consumeClick)
	{
	}
	
	public GuiBaseBookBrowser(P pagelet, PlayerSkillData data)
	{
		super(pagelet);
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		provideElements(texes::add);
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
	
	protected abstract void provideElements(Consumer<TX> handler);
	
	@Override
	protected void drawBack(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY)
	{
		var pose = gfx.pose();
		
		setWhiteColor(gfx);
		gui1.render(pose, guiLeft, guiTop);
		
		int co = texes.size();
		
		RenderSystem.enableBlend();
		scissorRect.setBounds(guiLeft, guiTop + 5, xSize, ySize - 10);
		Scissors.begin(guiLeft, guiTop + 5, xSize, ySize - 10);
		
		int cht = 0, chtni = 0;
		boolean singleHover = false;
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			var tex = texes.get(j);
			
			var hovt = hoverAnims.get(tex);
			if(hovt == null) hoverAnims.put(tex, hovt = new Tuple2.Mutable2<>(0, 0));
			
			int cHoverTime = hovt.a();
			int cHoverTimePrev = hovt.b();
			
			double x = (i % row) * 28 + guiLeft + 16;
			double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * partialTicks);
			if(y < -24) continue;
			if(y > ySize - 14) break;
			y += guiTop + 9;
			
			boolean hover = mouseX >= x && mouseX < x + 24 && mouseY >= y && mouseY < y + 24;
			
			if(hover)
			{
				currentHover = i;
				singleHover = true;
				
				chtni = cHoverTime;
			}
			
			float hoverProgress = 0F;
			
			if(cHoverTime > 0)
			{
				cht = (int) (cHoverTimePrev + (cHoverTime - cHoverTimePrev) * partialTicks);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(pose, x, y, 24, 24);
				
				RenderSystem.setShaderColor(1, 1, 1, hoverProgress = (float) Math.sin(Math.toRadians(cht / 255F * 90)));
				hov.render(pose, x, y, 24, 24);
				setWhiteColor(gfx);
			} else
				tex.toUV(false).render(pose, x, y, 24, 24);
			
			tex.renderDecorations(gfx, hoverProgress, x, y, partialTicks);
		}
		
		if(!singleHover)
			currentHover = -1;
		
		Scissors.end();
		
		setBlueColor(gfx);
		gui2.render(pose, guiLeft, guiTop, xSize, ySize);
		setWhiteColor(gfx);
		
		setWhiteColor(gfx);
		
		if(currentHover >= 0 && chtni >= 200)
			OTETooltip.showTooltip(texes.get(currentHover % co).getHoverTooltip());
	}
	
	protected double dWheel;
	
	@Override
	public boolean mouseScrolled(double x, double y, double dWheel)
	{
		this.dWheel += dWheel;
		return true;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		prevScrolledPixels = scrolledPixels;
		
		int co = texes.size();
		float maxPixels = 28 * (co / row) - 28 * 4;
		
		int dw = (int) dWheel * 100;
		if(dw != 0)
		{
			dWheel = 0;
			scrolledPixels -= dw * 14F / 100F;
			scrolledPixels = Math.max(Math.min(scrolledPixels, maxPixels), 0);
		}
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			var tex = texes.get(j);
			
			var hovt = hoverAnims.computeIfAbsent(tex, k -> Tuples.mutable(0, 0));
			
			int cHoverTime = hovt.a();
			int pht = cHoverTime;
			
			if(currentHover == i)
			{
				cHoverTime = Math.min(cHoverTime + 25, 255);
				
				double x = (i % row) * 28 + guiLeft + 16;
				double y = (i / row) * 28 - scrolledPixels;
				y += guiTop + 9;
				
				Random r = new Random();
				if(r.nextInt(3) == 0)
				{
					int[] rgbs = tex.getAllColors();
					int col = rgbs[r.nextInt(rgbs.length)];
					double tx = x + 2 + r.nextFloat() * 20F;
					double ty = y + 2 + r.nextFloat() * 20F;
					if(scissorRect.contains(x, y) && scissorRect.contains(tx, ty))
						OnTopEffects.effects.add(new OTESparkle(tx, ty, tx, ty, 11, col));
				}
			} else
				cHoverTime = Math.max(cHoverTime - 10, 0);
			
			hovt.setA(cHoverTime);
			hovt.setB(pht);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		if(currentHover >= 0)
		{
			var skill = texes.get(currentHover % texes.size());
			var feedback = skill.onMouseClicked(mouseButton);
			
			if(feedback.spawnFadeout)
			{
				int co = texes.size();
				for(int i = 0; i < co; ++i)
				{
					int j = i % co;
					var tex = texes.get(j);
					
					double x = (i % row) * 28 + guiLeft + 16;
					double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * minecraft.getPartialTick());
					
					if(tex == skill)
					{
						new OTEFadeOutUV(tex.toUV(true), 24, 24, x, y + guiTop + 9, 2);
						break;
					}
				}
			}
			
			if(feedback.playSound)
				minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundsIS.PAGE_TURNS, 1F));
			if(feedback.consumeClick)
				return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}