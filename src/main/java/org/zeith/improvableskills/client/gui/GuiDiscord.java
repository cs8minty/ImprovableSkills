package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.zeith.hammerlib.client.texture.HttpTextureDownloader;
import org.zeith.hammerlib.client.utils.RenderUtils;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.client.rendering.ote.OTEConfetti;
import org.zeith.improvableskills.custom.pagelets.PageletUpdate;
import org.zeith.improvableskills.init.SoundsIS;
import org.zeith.improvableskills.utils.Sys;

import java.util.Optional;

public class GuiDiscord
		extends GuiTabbable<PageletDiscord>
{
	public static boolean texureLoaded = false;
	public final UV gui1;
	
	
	private static final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/builtin/discord.png");
	
	public static AbstractTexture getDiscordServerIdTexture()
	{
		return HttpTextureDownloader.create(texture, "http://dccg.herokuapp.com/zmc", () ->
		{
			texureLoaded = true;
		});
	}
	
	public GuiDiscord(PageletDiscord pagelet)
	{
		super(pagelet);
		gui1 = new UV(new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		getDiscordServerIdTexture();
	}
	
	public int hoverTime;
	public boolean hovered;
	
	@Override
	public void tick()
	{
		super.tick();
		if(hovered && hoverTime < 10)
			++hoverTime;
		if(!hovered && hoverTime > 0)
			--hoverTime;
	}
	
	@Override
	protected void drawBack(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		setWhiteColor();
		gui1.render(pose, guiLeft, guiTop);
		
		boolean mouse = hovered = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) - 22 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) - 22 + xSize / 3.5;
		
		Optional.ofNullable(getDiscordServerIdTexture()).ifPresent(AbstractTexture::bind);
		
		if(texureLoaded)
		{
			float m = .67F + .33F * OTEConfetti.sineF(hoverTime / 10F);
			RenderSystem.setShaderColor(m, m, m, 1F);
			RenderUtils.drawFullTexturedModalRect(pose, guiLeft + (xSize - 3 * xSize / 3.5F) / 2, guiTop + (ySize - xSize / 3.5F) - 22, 3 * xSize / 3.5F, xSize / 3.5F);
			
			pose.pushPose();
			for(FormattedCharSequence formattedcharsequence : font.split(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":discord2"), xSize - 21))
			{
				font.draw(pose, formattedcharsequence, (int) guiLeft + 13, (int) guiTop + 12, 0xFF000000);
				pose.translate(0, 9, 0);
			}
			pose.popPose();
		} else
			GuiNewsBook.spawnLoading(width, height);
		
		setBlueColor();
		pose.pushPose();
		pose.translate(0, 0, 5);
		gui2.render(pose, guiLeft, guiTop);
		pose.popPose();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		boolean mouse = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) - 22 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) - 22 + xSize / 3.5;
		
		if(mouse)
		{
			Sys.openURL(PageletUpdate.discord);
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundsIS.CONNECT, 1.0F));
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}