package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.*;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.*;
import org.zeith.improvableskills.init.SoundsIS;

import java.util.*;

public class GuiAbilityBook
		extends GuiTabbable
		implements IGuiSkillDataConsumer
{
	public final UV gui1, inactivity;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<SkillTex<PlayerAbilityBase>, Tuple2.Mutable2<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int cHover;
	
	public PlayerSkillData data;
	public List<SkillTex<PlayerAbilityBase>> texes = new ArrayList<>();
	
	public GuiAbilityBook(PageletBase pagelet, PlayerSkillData data)
	{
		super(pagelet);
		
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(GuiSkillsBook.PAPER_TEXTURE, 0, 0, xSize, ySize);
		inactivity = new UV(GuiSkillViewer.TEXTURE, 195, 24, 20, 20);
		
		ImprovableSkills.ABILITIES().getValues()
				.stream()
				.sorted(Comparator.comparing(t -> t.getLocalizedName(data).getString()))
				.filter(data::hasAbility)
				.forEach(skill -> texes.add(skill.tex));
	}
	
	@Override
	protected void drawBack(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		setWhiteColor();
		gui1.render(pose, guiLeft, guiTop);
		
		int co = texes.size();
		
		RenderSystem.enableBlend();
		Scissors.begin(guiLeft, guiTop + 5, xSize, ySize - 10);
		
		int cht = 0, chtni = 0;
		boolean singleHover = false;
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			SkillTex<PlayerAbilityBase> tex = texes.get(j);
			
			Tuple2.Mutable2<Integer, Integer> hovt = hoverAnims.get(tex);
			if(hovt == null) hoverAnims.put(tex, hovt = new Tuple2.Mutable2<>(0, 0));
			
			int cHoverTime = hovt.a();
			int cHoverTimePrev = hovt.b();
			
			double x = (i % row) * 28 + guiLeft + 16;
			double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * partialTicks);
			
			if(y < -24)
				continue;
			
			if(y > ySize - 14)
				break;
			
			y += guiTop + 9;
			
			boolean hover = mouseX >= x && mouseX < x + 24 && mouseY >= y && mouseY < y + 24;
			
			if(hover)
			{
				cHover = i;
				singleHover = true;
				
				chtni = cHoverTime;
			}
			
			if(cHoverTime > 0)
			{
				cht = (int) (cHoverTimePrev + (cHoverTime - cHoverTimePrev) * partialTicks);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(pose, x, y, 24, 24);
				
				RenderSystem.setShaderColor(1, 1, 1, (float) Math.sin(Math.toRadians(cht / 255F * 90)));
				hov.render(pose, x, y, 24, 24);
				setWhiteColor();
			} else
				tex.toUV(false).render(pose, x, y, 24, 24);
			
			if(tex.owner.showDisabledIcon(data))
				inactivity.render(pose, x + 9.5F, y + 21, 5, 5);
		}
		
		if(!singleHover)
			cHover = -1;
		
		Scissors.end();
		
		setBlueColor();
		gui2.render(pose, guiLeft, guiTop, xSize, ySize);
		setWhiteColor();
		
		if(cHover >= 0 && chtni >= 200)
		{
			SkillTex<PlayerAbilityBase> tex = texes.get(cHover % co);
			OTETooltip.showTooltip(tex.owner.getLocalizedName(data));
		}
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
		float maxPixels = 28 * (co / row) - 28 * 7;
		
		int dw = (int) dWheel * 120;
		
		if(dw != 0)
		{
			dWheel = 0;
			scrolledPixels -= dw / 15F;
			scrolledPixels = Math.max(Math.min(scrolledPixels, maxPixels), 0);
		}
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			SkillTex<PlayerAbilityBase> tex = texes.get(j);
			
			Tuple2.Mutable2<Integer, Integer> hovt = hoverAnims.get(tex);
			if(hovt == null) hoverAnims.put(tex, hovt = new Tuple2.Mutable2<>(0, 0));
			
			int cHoverTime = hovt.a();
			int pht = cHoverTime;
			
			if(cHover == i)
			{
				cHoverTime = Math.min(cHoverTime + 55, 255);
				
				double x = (i % row) * 28 + guiLeft + 16;
				double y = (i / row) * 28 - scrolledPixels;
				y += guiTop + 9;
				
				Random r = new Random();
				if(r.nextInt(5) == 0)
				{
					int[] rgbs = TexturePixelGetter.getAllColors(tex.toUV(true).path);
					
					int col = rgbs[r.nextInt(rgbs.length)];
					double tx = x + 2 + r.nextFloat() * 20F;
					double ty = y + 2 + r.nextFloat() * 20F;
					OnTopEffects.effects.add(new OTESparkle(tx, ty, tx, ty, 11, col));
				}
			} else
				cHoverTime = Math.max(cHoverTime - 15, 0);
			
			hovt.setA(cHoverTime);
			hovt.setB(pht);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		if(cHover >= 0)
		{
			PlayerAbilityBase skill = texes.get(cHover % texes.size()).owner;
			
			skill.onClickClient(minecraft.player, mouseButton);
			
			int co = texes.size();
			for(int i = 0; i < co; ++i)
			{
				int j = i % co;
				SkillTex<PlayerAbilityBase> tex = texes.get(j);
				
				double x = (i % row) * 28 + guiLeft + 16;
				double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * minecraft.getPartialTick());
				
				if(tex == skill.tex)
					new OTEFadeOutUV(tex.toUV(true), 24, 24, x, y + guiTop + 9, 2);
			}
			
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundsIS.PAGE_TURNS, 1F));
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
}