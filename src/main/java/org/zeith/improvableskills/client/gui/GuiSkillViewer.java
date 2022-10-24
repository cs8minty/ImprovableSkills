package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.*;
import org.zeith.improvableskills.init.SoundsIS;
import org.zeith.improvableskills.net.PacketLvlDownSkill;
import org.zeith.improvableskills.net.PacketLvlUpSkill;

import java.util.Random;

public class GuiSkillViewer
		extends GuiCentered
		implements IGuiSkillDataConsumer
{
	final GuiSkillsBook parent;
	public PlayerSkillData data;
	final Style fontStyle;
	final PlayerSkillBase skill;
	
	int mouseX, mouseY;
	boolean forbidden;
	
	private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
	
	public GuiSkillViewer(GuiSkillsBook parent, PlayerSkillBase skill)
	{
		this.parent = parent;
		this.skill = skill;
		this.data = parent.data;
		this.minecraft = Minecraft.getInstance();
		fontStyle = skill.isVisible(parent.data) ? Style.EMPTY : Style.EMPTY.withFont(ALT_FONT);
		xSize = 200;
		ySize = 150;
	}
	
	GuiCustomButton btn0, btn1, btn2;
	
	@Override
	public void init()
	{
		super.init();
		parent.init();
		
		int gl = guiLeft, gt = guiTop;
		
		btn0 = addRenderableWidget(new GuiCustomButton(0, gl + 10, gt + 124, 75, 20, Component.translatable("button.improvableskills:upgrade"), this::actionPerformed));
		btn1 = addRenderableWidget(new GuiCustomButton(1, gl + 116, gt + 124, 75, 20, Component.translatable("button.improvableskills:degrade"), this::actionPerformed));
		btn2 = addRenderableWidget(new GuiCustomButton(2, gl + (xSize - 20) / 2, gt + 124, 20, 20, " ", this::actionPerformed).setCustomClickSound(SoundsIS.PAGE_TURNS));
	}
	
	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		forbidden = !skill.isVisible(parent.data);
		
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		
		short nsl = (short) (data.getSkillLevel(skill) + 1);
		int xp = skill.getXPToUpgrade(data, nsl);
		int xp2 = skill.getXPToDowngrade(data, (short) (nsl - 2));
		
		boolean max = nsl <= skill.maxLvl && !forbidden;
		btn0.active = max && skill.canUpgrade(data);
		btn1.active = data.getSkillLevel(skill) > 0 && !forbidden;
		
		super.render(pose, mouseX, mouseY, partialTicks);
		
		FXUtils.bindTexture(ImprovableSkills.MOD_ID, "textures/gui/skills_gui_overlay.png");
		pose.pushPose();
		pose.translate(guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 200);
		pose.scale(1.5F, 1.5F, 1);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, 195, 10, 10, 11);
		pose.popPose();
		
		drawCenteredString(pose, forbidden ? minecraft.fontFilterFishy : font, I18n.get("text.improvableskills:totalXP", XPUtil.getXPTotal(minecraft.player)), guiLeft + xSize / 2, guiTop + ySize + 2, 0x88FF00);
		
		if(btn0.isMouseOver(mouseX, mouseY) && max)
			OTETooltip.showTooltip(Component.literal("-" + xp + " XP"));
		
		if(btn1.isMouseOver(mouseX, mouseY) && btn1.active)
			OTETooltip.showTooltip(Component.literal("+" + xp2 + " XP"));
		
		if(btn2.isMouseOver(mouseX, mouseY))
			OTETooltip.showTooltip(Component.translatable("gui.back"));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		var name = skill.getLocalizedName(data);
		
		renderBackground(pose);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		pose.pushPose();
		pose.translate(guiLeft, guiTop, 0);
		FXUtils.bindTexture(ImprovableSkills.MOD_ID, "textures/gui/skill_viewer.png");
		RenderUtils.drawTexturedModalRect(pose, 0, 0, 0, 0, xSize, ySize);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		skill.tex.toUV(false).render(pose, 10, 6, 32, 32);
		int lev = data.getSkillLevel(skill);
		if(lev > 0)
		{
			var hov = skill.tex.toUV(true);
			RenderSystem.setShaderColor(1, 1, 1, (float) lev / skill.maxLvl);
			hov.render(pose, 10, 6, 32, 32);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
		
		font.draw(pose, I18n.get("text.improvableskills:level", data.getSkillLevel(skill), skill.maxLvl), 44, 30, 0x555555);
		
		float scale = Math.min((xSize - 48) / font.width(name), 1.5F);
		double flh = font.lineHeight * scale;
		pose.translate(44, 6 + (24 - flh) / 2, 0);
		pose.scale(scale, scale, 1);
		font.draw(pose, name, 0, 0, 0x555555);
		pose.popPose();
		
		int maxWid = 176;
		
		pose.pushPose();
		pose.translate(0, 2, 0);
		for(FormattedCharSequence formattedcharsequence : font.split(skill.getLocalizedDesc(data), maxWid))
		{
			font.draw(pose, formattedcharsequence, guiLeft + 12, guiTop + 42, 0xFFFFFF);
			pose.translate(0, 9, 0);
		}
		pose.popPose();
	}
	
	protected void actionPerformed(Button button)
	{
		if(!(button instanceof GuiCustomButton b)) return;
		
		new OTEFadeOutButton(b, b.id == 2 ? 2 : 20);
		
		if(b.id == 2)
		{
			minecraft.setScreen(parent);
			new OTEFadeOutUV(new UV(new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/skills_gui_overlay.png"), 195, 10, 10, 11), 10 * 1.5F, 11 * 1.5F, guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 2);
		}
		
		if(b.id == 0)
		{
			Random r = new Random();
			
			int[] rgbs = TexturePixelGetter.getAllColors(skill.tex.toUV(true).path);
			
			int col = rgbs[r.nextInt(rgbs.length)];
			double tx = guiLeft + 10 + r.nextInt(64) / 2F;
			double ty = guiTop + 6 + r.nextInt(64) / 2F;
			OnTopEffects.effects.add(new OTESparkle(mouseX, mouseY, tx, ty, 30, col));
			
			Network.sendToServer(new PacketLvlUpSkill(skill));
		}
		
		if(b.id == 1)
		{
			Random r = new Random();
			
			int[] rgbs = TexturePixelGetter.getAllColors(skill.tex.toUV(true).path);
			
			int col = rgbs[r.nextInt(rgbs.length)];
			double tx = guiLeft + 10 + r.nextInt(64) / 2F;
			double ty = guiTop + 6 + r.nextInt(64) / 2F;
			OnTopEffects.effects.add(new OTESparkle(tx, ty, mouseX, mouseY, 30, col));
			
			Network.sendToServer(new PacketLvlDownSkill(skill));
		}
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
		this.parent.data = data;
	}
	
	public void drawHoveringText(PoseStack pose, String text, int x, int y)
	{
		renderTooltip(pose, Component.literal(text).withStyle(forbidden ? Style.EMPTY.withFont(ALT_FONT) : Style.EMPTY), x, y);
	}
}