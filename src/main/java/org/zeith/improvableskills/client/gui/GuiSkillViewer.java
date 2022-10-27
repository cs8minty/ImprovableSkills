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
import org.zeith.improvableskills.net.*;

import java.util.Random;

public class GuiSkillViewer
		extends GuiCentered
		implements IGuiSkillDataConsumer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/skills_gui_overlay.png");
	
	public static final UV CROSS = new UV(TEXTURE, 196, 24, 20, 20);
	public static final UV TICK = new UV(TEXTURE, 196, 44, 20, 20);
	
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
	
	GuiCustomButton btnUpgrade, btnDegrade, btnBack, btnToggle;
	
	@Override
	public void init()
	{
		super.init();
		parent.init();
		
		int gl = guiLeft, gt = guiTop;
		
		btnUpgrade = addRenderableWidget(new GuiCustomButton(0, gl + 10, gt + 124, 75, 20, Component.translatable("button.improvableskills:upgrade"), this::actionPerformed));
		btnDegrade = addRenderableWidget(new GuiCustomButton(1, gl + 116, gt + 124, 75, 20, Component.translatable("button.improvableskills:degrade"), this::actionPerformed));
		btnBack = addRenderableWidget(new GuiCustomButton(2, gl + (xSize - 20) / 2, gt + 124, 20, 20, " ", this::actionPerformed).setCustomClickSound(SoundsIS.PAGE_TURNS));
		btnToggle = addRenderableWidget(new GuiCustomButton(3, gl + xSize - 30, gt + 14, 20, 20, " ", this::actionPerformed).setCustomClickSound(SoundsIS.PAGE_TURNS));
	}
	
	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		forbidden = !skill.isVisible(parent.data);
		
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		
		short nextSkillLevel = (short) (data.getSkillLevel(skill) + 1);
		int xp = skill.getXPToUpgrade(data, nextSkillLevel);
		int xp2 = skill.getXPToDowngrade(data, (short) (nextSkillLevel - 2));
		btnToggle.active = nextSkillLevel > 1;
		
		boolean notMaxedOut = nextSkillLevel <= skill.getMaxLevel() && !forbidden;
		
		super.render(pose, mouseX, mouseY, partialTicks);
		
		FXUtils.bindTexture(TEXTURE);
		pose.pushPose();
		pose.translate(guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 200);
		pose.scale(1.5F, 1.5F, 1);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, 195, 10, 10, 11);
		pose.popPose();
		
		var active = data.isSkillActive(skill);
		
		pose.pushPose();
		pose.translate(btnToggle.x + 1, btnToggle.y + 1, 200);
		pose.scale(18F / 20F, 18F / 20F, 18F / 20F);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, CROSS.posX, CROSS.posY + (active ? 20 : 0), 20, 20);
		pose.popPose();
		
		drawCenteredString(pose, forbidden ? minecraft.fontFilterFishy : font, I18n.get("text.improvableskills:totalXP", XPUtil.getXPTotal(minecraft.player)), guiLeft + xSize / 2, guiTop + ySize + 2, 0x88FF00);
		
		btnUpgrade.active = true;
		
		if(btnUpgrade.isMouseOver(mouseX, mouseY) && notMaxedOut)
			OTETooltip.showTooltip(Component.literal("-" + xp + " XP"));
		
		if(btnDegrade.isMouseOver(mouseX, mouseY))
			OTETooltip.showTooltip(Component.literal("+" + xp2 + " XP"));
		
		btnUpgrade.active = notMaxedOut && skill.canUpgrade(data);
		btnDegrade.active = data.getSkillLevel(skill) > 0 && !forbidden;
		
		if(btnBack.isMouseOver(mouseX, mouseY))
			OTETooltip.showTooltip(Component.translatable("gui.back"));
		
		if(btnToggle.isMouseOver(mouseX, mouseY))
			OTETooltip.showTooltip(Component.translatable("gui." + ImprovableSkills.MOD_ID + ".toggle_skill." + (active ? "enabled" : "disabled"), skill.getLocalizedName()));
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
			RenderSystem.setShaderColor(1, 1, 1, (float) lev / skill.getMaxLevel());
			hov.render(pose, 10, 6, 32, 32);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
		
		font.draw(pose, I18n.get("text.improvableskills:level", data.getSkillLevel(skill), skill.getMaxLevel()), 44, 30, 0x555555);
		
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
			new OTEFadeOutUV(new UV(TEXTURE, 195, 10, 10, 11), 10 * 1.5F, 11 * 1.5F, guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 2);
		}
		
		if(b.id == 3)
		{
			var newState = !data.isSkillActive(skill);
			data.setSkillState(skill, newState);
			new OTEFadeOutUV(new UV(TEXTURE, CROSS.posX, CROSS.posY + (newState ? 20 : 0), 20, 20), 18, 18, b.x + 1, b.y + 1, 20);
			Network.sendToServer(new PacketSetSkillActivity(skill.getRegistryName(), newState));
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