package org.zeith.improvableskills.client.gui;

import com.google.common.base.Joiner;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.zeith.hammerlib.client.utils.Scissors;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.hammerlib.util.colors.Rainbow;
import org.zeith.hammerlib.util.java.Hashers;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.OTESparkle;
import org.zeith.improvableskills.custom.pagelets.PageletNews;
import org.zeith.improvableskills.data.ClientData;
import org.zeith.improvableskills.utils.GoogleTranslate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GuiNewsBook
		extends GuiTabbable<PageletNews>
{
	public final UV gui1;
	public Component changes, translated;
	
	public GuiNewsBook(PageletNews pagelet)
	{
		super(pagelet);
		
		gui1 = new UV(new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		
		reload();
	}
	
	public String getOrTranslate(String changes)
	{
		String sha = Hashers.SHA256.hashify(changes);
		
		String lng = Minecraft.getInstance().getLocale().getLanguage();
		
		String stored = ClientData.readData("news_" + lng + ".sha").orElse(null);
		String olng = ClientData.readData("news_" + lng + ".txt").orElse(null);
		
		if(!sha.equalsIgnoreCase(stored) || olng == null)
		{
			List<String> s = new ArrayList<>();
			for(String ln : changes.split("\n"))
			{
				try
				{
					if(!lng.equals("en"))
						ln = GoogleTranslate.translate(lng, ln);
				} catch(IOException ignored)
				{
				}
				s.add(ln);
			}
			
			String ts = Joiner.on("\n").join(s);
			
			ClientData.writeData("news.sha", sha);
			ClientData.writeData("news_" + lng + ".sha", sha);
			ClientData.writeData("news_" + lng + ".txt", ts);
			try
			{
				Field f = PageletNews.class.getDeclaredField("popping");
				f.setAccessible(true);
				f.setBoolean(pagelet, false);
			} catch(Exception ignored)
			{
			}
			return ts;
		}
		
		return olng;
	}
	
	public void reload()
	{
		changes = null;
		translated = null;
		
		Threading.createAndStart(() ->
		{
			var ch = pagelet.getChanges();
			if(ch == null) changes = Component.literal("");
			else changes = Component.literal(ch);
			this.translated = Component.literal(getOrTranslate(pagelet.getChanges()));
		});
	}
	
	@Override
	protected void drawBack(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		setWhiteColor();
		gui1.render(pose, guiLeft, guiTop);
		
		RenderSystem.enableBlend();
		Scissors.begin(guiLeft, guiTop + 5, xSize, ySize - 10);
		
		var translated = this.translated;
		
		if(hasShiftDown() || hasAltDown() || hasControlDown())
		{
			var s = pagelet.getChanges();
			if(s != null)
				translated = Component.literal(s);
		}
		
		if(translated != null)
		{
			pose.pushPose();
			for(FormattedCharSequence formattedcharsequence : font.split(translated, (int) gui1.width - 22))
			{
				font.draw(pose, formattedcharsequence, (int) guiLeft + 12, (int) guiTop + 12, 0xFF000000);
				pose.translate(0, 9, 0);
			}
			pose.popPose();
		} else
			spawnLoading(width, height);
		
		RenderSystem.enableDepthTest();
		Scissors.end();
		
		setBlueColor();
		pose.pushPose();
		pose.translate(0, 0, 5);
		gui2.render(pose, guiLeft, guiTop);
		pose.popPose();
		
		setWhiteColor();
	}
	
	public static void spawnLoading(float width, float height)
	{
		Minecraft mc = Minecraft.getInstance();
		float partialTicks = mc.getPartialTick();
		
		int dots = 3;
		float angle = 360 / dots;
		float degree = ((mc.player.tickCount + partialTicks) * 3) % 360F;
		
		float x = width / 2, y = height / 2;
		float rad = 48;
		
		for(int i = 0; i < dots; ++i)
		{
			double ax = x + Math.sin(Math.toRadians(degree)) * rad, ay = y + Math.cos(Math.toRadians(degree)) * rad;
			
			double oax = x + Math.sin(Math.toRadians(degree - 30)) * rad, oay = y + Math.cos(Math.toRadians(degree - 30)) * rad;
			
			if(Math.random() < .25)
				OnTopEffects.effects.add(new OTESparkle(ax, ay, oax, oay, 20, 255 << 24 | Rainbow.doIt(i * 1000 / dots, 1000L)));
			
			degree += angle;
		}
	}
}