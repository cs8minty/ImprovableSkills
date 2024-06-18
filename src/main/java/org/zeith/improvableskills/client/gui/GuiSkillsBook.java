package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.OwnedTexture;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.client.IClientSkillExtensions;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.custom.pagelets.PageletSkills;
import org.zeith.improvableskills.net.PacketSetSkillActivity;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class GuiSkillsBook
		extends GuiBaseBookBrowser<GuiSkillsBook.SkillTxInstance, PageletSkills>
{
	public final UV
			medal = new UV(GuiSkillViewer.TEXTURE, xSize + 1, 0, 10, 10),
			inactivity = GuiSkillViewer.CROSS;
	
	public GuiSkillsBook(PageletSkills pagelet, PlayerSkillData data)
	{
		super(pagelet, data);
	}
	
	@Override
	protected void provideElements(Consumer<SkillTxInstance> handler)
	{
		ImprovableSkills.SKILLS
				.stream()
				.sorted(Comparator.comparing(t -> t.getLocalizedName(data).getString()))
				.filter(skill -> skill.isVisible(data))
				.forEach(skill -> handler.accept(new SkillTxInstance(skill.tex)));
	}
	
	public class SkillTxInstance
			implements GuiBaseBookBrowser.ITxInstance
	{
		final OwnedTexture<PlayerSkillBase> tex;
		
		public SkillTxInstance(OwnedTexture<PlayerSkillBase> tex)
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
			var sr = IClientSkillExtensions.of(tex.owner).slotRenderer();
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
			if(button == 0)
				minecraft.pushGuiLayer(new GuiSkillViewer(GuiSkillsBook.this, tex.owner));
			else if(button == 1)
			{
				var newState = !data.isSkillActive(tex.owner);
				data.setSkillState(tex.owner, newState);
				Network.sendToServer(new PacketSetSkillActivity(tex.owner.getRegistryName(), newState));
			}
			
			return new GuiBaseBookBrowser.ClickFeedback(true, true, true);
		}
		
		@Override
		public void renderDecorations(GuiGraphics gfx, float hoverProgress, double x, double y, float partialTicks)
		{
			var pose = gfx.pose();
			
			if(data.getSkillLevel(tex.owner) >= tex.owner.getMaxLevel())
				medal.render(pose, x + 15, y + 17, 10, 10);
			
			if(!data.isSkillActive(tex.owner))
				inactivity.render(pose, x + 9.5F, y + 21, 5, 5);
			
			if(tex.owner.getScrollState().hasScroll())
			{
				pose.pushPose();
				pose.translate(x + 0.5F, y + 19.5F, 0);
				pose.scale(1 / 2F, 1 / 2F, 1);
				RenderSystem.enableDepthTest();
				RenderUtils.renderItemIntoGui(pose, ItemSkillScroll.of(tex.owner), 0, 0);
				RenderSystem.disableDepthTest();
				pose.popPose();
			}
		}
	}
}