package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutButton;
import org.zeith.improvableskills.client.rendering.ote.OTEXpOrb;
import org.zeith.improvableskills.custom.pagelets.PageletXPStorage;
import org.zeith.improvableskills.init.PageletsIS;
import org.zeith.improvableskills.net.PacketDrawXP;
import org.zeith.improvableskills.net.PacketStoreXP;

import java.math.BigInteger;

public class GuiXPBank
		extends GuiTabbable<PageletXPStorage>
		implements IGuiSkillDataConsumer
{
	public PlayerSkillData data;
	public double targetXP_X, targetXP_Y;
	public float currentXP, prevXP;
	
	public GuiXPBank(PageletXPStorage pagelet)
	{
		super(pagelet);
		this.data = SyncSkills.getData();
		xSize = 195;
		ySize = 168;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		int guiLeft = this.guiLeft;
		int guiTop = this.guiTop;
		
		GuiCustomButton btn, btn2;
		
		int sizeX = 20;
		
		int scrW = xSize - sizeX * 2;
		
		var storeAll = Component.translatable("text." + ImprovableSkills.MOD_ID + ":storeall");
		var draw10 = Component.translatable("text." + ImprovableSkills.MOD_ID + ":draw10lvls");
		var draw1 = Component.translatable("text." + ImprovableSkills.MOD_ID + ":draw1lvl");
		
		addRenderableWidget(btn = new GuiCustomButton(1, guiLeft + 21, guiTop + 39, 100, 20, storeAll, this::actionPerformed).setCustomClickSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
		btn.setWidth(font.width(storeAll) + 8);
		
		var wid = font.width(draw10);
		addRenderableWidget(btn2 = new GuiCustomButton(3, guiLeft + sizeX + scrW - wid - 13, guiTop + btn.getHeight() + 30, wid + 12, 20, draw10, this::actionPerformed).setCustomClickSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
		addRenderableWidget(new GuiCustomButton(2, btn2.getX(), guiTop + 28, btn2.getWidth(), 20, draw1.append(" "), this::actionPerformed).setCustomClickSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		BigInteger i = data.storageXp;
		int xp = i.intValue();
		float current = XPUtil.getCurrentFromXPValue(xp);
		
		if(this.currentXP == 0F && this.prevXP == 0F)
			this.currentXP = this.prevXP = current;
		else
		{
			this.prevXP = this.currentXP;
			this.currentXP = current;
		}
	}
	
	public final ResourceLocation ICONS_TX = new ResourceLocation("improvableskills", "textures/gui/skills_gui_paper.png");
	
	@Override
	protected void drawBack(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY)
	{
		var pose = gfx.pose();
		setWhiteColor(gfx);
		gui1.render(pose, guiLeft, guiTop);
		
		int guiLeft = this.guiLeft;
		int guiTop = this.guiTop;
		
		int sizeX = 20;
		int sizeY = 26;
		
		gfx.setColor(0.8F, 0.8F, 0.8F, 1);
		gfx.blit(ICONS_TX, guiLeft + sizeX, guiTop + sizeY - 9, sizeX, sizeY, xSize - sizeX * 2, ySize - sizeY * 2 - 4 + 14);
		
		var form = PageletsIS.XP_STORAGE.getTitle().copy().withStyle(ChatFormatting.BLACK).append(": " + data.storageXp + " XP");
		gfx.drawString(font, form, guiLeft + (xSize - font.width(form)) / 2, guiTop + 9, 0x3F7F00, false);
		
		setWhiteColor(gfx);
		
		FXUtils.bindTexture("minecraft", "textures/gui/icons.png");
		
		double bx = xSize / 2 - 72.8D;
		double by = ySize - 50;
		BigInteger i = data.storageXp;
		int xp = i.intValue();
		
		pose.pushPose();
		pose.translate(targetXP_X = guiLeft + bx, targetXP_Y = guiTop + by + 18, 0.0D);
		pose.scale(0.8F, 0.8F, 0.8F);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, 0, 64, 182, 5);
		RenderUtils.drawTexturedModalRect(pose, 0, 0, 0, 69, 182.0F * (prevXP + (currentXP - prevXP) * partialTicks), 5);
		pose.popPose();
		
		pose.pushPose();
		pose.translate(guiLeft + bx, guiTop + by + 18, 0.0D);
		pose.scale(1.1F, 1.1F, 1.1F);
		int lvl = XPUtil.getLevelFromXPValue(xp);
		var text = (lvl < 0 ? "TOO MUCH!!!" : Integer.toString(lvl));
		RenderSystem.setShaderColor(0.24705882F, 0.49803922F, 0.0F, 1F);
		gfx.drawString(font, text, (145.6F - font.width(text)) / 2.0F * 0.9F, -8.0F, 0x3f7f00, false);
		setWhiteColor(gfx);
		pose.popPose();
		
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		gfx.drawCenteredString(font, I18n.get("text." + ImprovableSkills.MOD_ID + ":totalXP", XPUtil.getXPTotal(minecraft.player)), guiLeft + xSize / 2, guiTop + ySize + 4, (int) (r * 255.0F) << 16 | 0xFF00 | 0x0);
		
		setBlueColor(gfx);
		gui2.render(pose, guiLeft, guiTop, xSize, ySize);
		setWhiteColor(gfx);
	}
	
	protected void actionPerformed(Button buttonIn)
	{
		if(!(buttonIn instanceof GuiCustomButton button))
			return;
		
		int id = button.id;
		
		new OTEFadeOutButton(button, id == 0 ? 2 : 20);
		
		if(id == 0)
			minecraft.setScreen(parent);
		
		else if(id == 1)
		{
			var rand = minecraft.player.getRandom();
			
			int lvls = minecraft.player.experienceLevel;
			
			for(int i = 0; i < Math.min(100, lvls); ++i)
			{
				double rtx;
				double rty;
				
				rtx = targetXP_X + rand.nextFloat() * 182F * .8F;
				rty = targetXP_Y + rand.nextFloat() * 5F;
				
				double rx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
				double ry = guiTop + ySize + 4 + rand.nextFloat() * font.lineHeight;
				
				OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
			}
			
			Network.sendToServer(new PacketStoreXP(XPUtil.getXPTotal(minecraft.player)));
		} else if(id == 2)
		{
			Network.sendToServer(new PacketDrawXP(XPUtil.getXPValueToNextLevel(XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(minecraft.player)))));
			
			if(data.storageXp.longValue() > 0L)
			{
				var rand = minecraft.player.getRandom();
				
				double rx;
				double ry;
				
				BigInteger i = data.storageXp;
				int xp = i.intValue();
				float current = XPUtil.getCurrentFromXPValue(xp);
				
				rx = targetXP_X + rand.nextFloat() * 182F * current * .8F;
				ry = targetXP_Y + rand.nextFloat() * 5F;
				
				double rtx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
				double rty = guiTop + ySize + 4 + rand.nextFloat() * font.lineHeight;
				
				OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
			}
		} else if(id == 3)
		{
			var rand = minecraft.player.getRandom();
			
			int xpLvl = XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(minecraft.player));
			int xp = 0;
			for(int i = 0; i < 10; i++)
			{
				BigInteger i0 = data.storageXp;
				int xp0 = i0.intValue();
				float current = XPUtil.getCurrentFromXPValue(xp0);
				
				if(data.storageXp.longValue() > 0L)
				{
					double rx;
					double ry;
					
					rx = targetXP_X + rand.nextFloat() * 182F * current * .8F;
					ry = targetXP_Y + rand.nextFloat() * 5F;
					
					double rtx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
					double rty = guiTop + ySize + 4 + rand.nextFloat() * font.lineHeight * .8;
					
					OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
				}
				
				xp += XPUtil.getXPValueToNextLevel(xpLvl + i);
			}
			
			Network.sendToServer(new PacketDrawXP(xp));
		}
	}
}