package org.zeith.improvableskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.zeith.hammerlib.client.utils.Scissors;
import org.zeith.hammerlib.util.java.Hashers;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.custom.pagelets.PageletUpdate;
import org.zeith.improvableskills.data.ClientData;
import org.zeith.improvableskills.utils.GoogleTranslate;
import org.zeith.improvableskills.utils.Sys;

import java.io.IOException;
import java.util.*;

public class GuiUpdateBook
		extends GuiTabbable<PageletUpdate>
{
	public int scroll;
	public MutableComponent changes, translated;
	
	public GuiUpdateBook(PageletUpdate pagelet)
	{
		super(pagelet);
		
		reload();
	}
	
	private static Map<String, String> cache = new HashMap<>();
	
	public String getOrTranslate(String changes)
	{
		if(cache.containsKey(changes))
			return cache.get(changes);
		
		String sha256 = Hashers.SHA256.hashify(changes);
		
		String lng = Minecraft.getInstance().getLocale().getLanguage();
		
		var lang = "update_" + lng + ".txt";
		var langSHA = "update_" + lng + ".sha";
		
		var stored = ClientData.readData(langSHA).orElse(null);
		var translatedCache = ClientData.readData(lang).orElse(null);
		
		if(!sha256.equalsIgnoreCase(stored) || translatedCache == null)
		{
			List<String> s = new ArrayList<>();
			for(String ln : changes.split("\n"))
			{
				try
				{
					if(!lng.equals("en"))
						ln = GoogleTranslate.translate(lng, ln);
				} catch(IOException ioe)
				{
				}
				s.add(ln);
			}
			String ts = String.join("\n", s);
			ClientData.writeData(langSHA, sha256);
			ClientData.writeData(lang, ts);
			return ts;
		}
		
		return translatedCache;
	}
	
	public void reload()
	{
		changes = null;
		translated = null;
		
		Threading.createAndStart(() ->
		{
			pagelet.reload();
			pagelet.joinReload();
			
			String ts = PageletUpdate.changes;
			changes = Component.literal(ts);
			try
			{
				String c = "\u25BA ";
				ts = getOrTranslate(PageletUpdate.changes)
						.replace("\n\n", "\n")
						.replace("\n", "\n" + c);
			} catch(Throwable er)
			{
				er.printStackTrace();
			}
			this.translated = Component.literal(ts);
		});
	}
	
	@Override
	protected void drawBack(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY)
	{
		var pose = gfx.pose();
		
		setWhiteColor(gfx);
		gui1.render(pose, guiLeft, guiTop);
		
		RenderSystem.enableBlend();
		Scissors.begin(guiLeft, guiTop + 5, xSize, ySize - 10);
		
		var upd = Component.literal(I18n.get("gui." + ImprovableSkills.MOD_ID + ":nver") + ": " + PageletUpdate.latest);
		boolean dwnHover = mouseY >= guiTop + 8 && mouseY < guiTop + ySize - 11 && mouseX >= guiLeft + 16 && mouseY >= guiTop + 11 - scroll && mouseX < guiLeft + 16 + font.width(upd) && mouseY < guiTop + 11 - scroll + font.lineHeight;
		
		if(translated != null)
		{
			pose.pushPose();
			
			var comp = upd.withStyle((dwnHover ? ChatFormatting.BLUE : ChatFormatting.RESET), ChatFormatting.UNDERLINE);
			for(FormattedCharSequence formattedcharsequence : font.split(comp, (int) gui1.width - 22))
			{
				gfx.drawString(font, formattedcharsequence, (int) guiLeft + 16, (int) guiTop + 11 - scroll, 0xFF000000, false);
				pose.translate(0, 9, 0);
			}
			
			comp = translated;
			for(FormattedCharSequence formattedcharsequence : font.split(comp, (int) gui1.width - 22))
			{
				gfx.drawString(font, formattedcharsequence, (int) guiLeft + 12, (int) guiTop + 12 - scroll, 0xFF000000, false);
				pose.translate(0, 9, 0);
			}
			
			pose.popPose();
		} else
			GuiNewsBook.spawnLoading(width, height);
		
		RenderSystem.enableDepthTest();
		Scissors.end();
		
		setBlueColor(gfx);
		pose.pushPose();
		pose.translate(0, 0, 5);
		gui2.render(pose, guiLeft, guiTop);
		pose.popPose();
		
		setWhiteColor(gfx);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		var upd = Component.literal(I18n.get("gui." + ImprovableSkills.MOD_ID + ":nver") + ": " + PageletUpdate.latest);
		boolean dwnHover = mouseY >= guiTop + 8 && mouseY < guiTop + ySize - 11 && mouseX >= guiLeft + 16 && mouseY >= guiTop + 11 - scroll && mouseX < guiLeft + 16 + font.width(upd) && mouseY < guiTop + 11 - scroll + font.lineHeight;
		
		if(dwnHover)
		{
			minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.SLIME_SQUISH_SMALL, 1F));
			Sys.openURL(PageletUpdate.homepage + "/files");
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double dWheel)
	{
		int dw = (int) ((dWheel * 120) / -30);
		if(dw != 0)
		{
			scroll += dw;
			int totHe = Math.max(font.split(translated, (int) gui1.width - 22).size() * font.lineHeight - ((int) ySize - 36), 0);
			scroll = Math.min(Math.max(0, scroll), totHe);
		}
		return true;
	}
}