package org.zeith.improvableskills.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.OwnedTexture;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.client.IClientAbilityExtensions;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.custom.pagelets.PageletAbilities;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class GuiAbilityBook
		extends GuiBaseBookBrowser<GuiAbilityBook.AbilityTxInstance, PageletAbilities>
{
	public GuiAbilityBook(PageletAbilities pagelet, PlayerSkillData data)
	{
		super(pagelet, data);
	}
	
	@Override
	protected void provideElements(Consumer<AbilityTxInstance> handler)
	{
		ImprovableSkills.ABILITIES
				.stream()
				.sorted(Comparator.comparing(t -> t.getLocalizedName(data).getString()))
				.filter(data::hasAbility)
				.forEach(ab -> handler.accept(new AbilityTxInstance(ab.tex)));
	}
	
	public class AbilityTxInstance
			implements GuiBaseBookBrowser.ITxInstance
	{
		final OwnedTexture<PlayerAbilityBase> tex;
		
		public AbilityTxInstance(OwnedTexture<PlayerAbilityBase> tex)
		{
			this.tex = tex;
		}
		
		@Override
		public UV getHoverUV()
		{
			return tex.toUV(true);
		}
		
		@Override
		public void drawUV(GuiGraphics gfx, float x, float y, float width, float height, float hoverProgress, float partialTicks)
		{
			var sr = IClientAbilityExtensions.of(tex.owner).slotRenderer();
			if(sr.drawSlot(gfx, x, y, width, height, hoverProgress, partialTicks))
				return;
			
			var pose = gfx.pose();
			UV norm = tex.toUV(false);
			norm.render(pose, x, y, width, height);
			
			if(hoverProgress > 0)
			{
				UV hov = tex.toUV(true);
				gfx.setColor(1, 1, 1, hoverProgress);
				hov.render(pose, x, y, width, height);
				gfx.setColor(1F, 1F, 1F, 1F);
			}
		}
		
		@Override
		public List<Component> getHoverTooltip()
		{
			return List.of(tex.owner.getLocalizedName());
		}
		
		@Override
		public GuiBaseBookBrowser.ClickFeedback onMouseClicked(int button)
		{
			tex.owner.onClickClient(minecraft.player, button);
			return new GuiBaseBookBrowser.ClickFeedback(true, true, true);
		}
		
		@Override
		public void renderDecorations(GuiGraphics gfx, float hoverProgress, double x, double y, float partialTicks)
		{
			var pose = gfx.pose();
			
			if(tex.owner.showDisabledIcon(data))
				GuiSkillViewer.CROSS.render(pose, x + 9.5F, y + 21, 5, 5);
		}
	}
}