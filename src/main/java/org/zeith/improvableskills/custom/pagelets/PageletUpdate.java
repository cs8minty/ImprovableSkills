package org.zeith.improvableskills.custom.pagelets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.hammerlib.util.java.net.HttpRequest;
import org.zeith.hammerlib.util.shaded.json.JSONObject;
import org.zeith.hammerlib.util.shaded.json.JSONTokener;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.GuiUpdateBook;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;

import static net.minecraftforge.fml.VersionChecker.Status.*;

public class PageletUpdate
		extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/update.png");
	public static VersionChecker.Status level;
	public static String changes, latest, homepage;
	public static String liveURL, liveTitle;
	
	{
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":update"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isRight()
	{
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesPop()
	{
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Object getIcon()
	{
		Object o = super.getIcon();
		if(!(o instanceof AbstractTexture))
			setIcon(o = Minecraft.getInstance().getTextureManager().getTexture(texture));
		return o;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return new GuiUpdateBook(this);
	}
	
	Thread reloadThread;
	
	@Override
	public void reload()
	{
		if(reloadThread != null)
			return;
		
		reloadThread = Threading.createAndStart(() ->
		{
			try
			{
				JSONObject o = (JSONObject) new JSONTokener(HttpRequest.get("https://api.modrinth.com/updates/9fT7HUaI/forge_updates.json").body()).nextValue();
				
				var mcVersion = FMLLoader.versionInfo().mcVersion();
//				changes = new String(Base64.getDecoder().decode(o.optJSONObject("changelogs64").optString(mcVersion + "-latest")));
				changes = "";
				homepage = o.getString("homepage");
				
				var promos = o.getJSONObject("promos");
				String rec = promos.optString(mcVersion + "-recommended");
				String lat = promos.optString(mcVersion + "-latest");
				
				latest = lat;
				
				var mod = ModList.get().getModFileById(ImprovableSkills.MOD_ID).getMods().get(0);
				ComparableVersion current = new ComparableVersion(mod.getVersion().toString());
				ComparableVersion recommended = new ComparableVersion(rec);
				int diff = recommended.compareTo(current);
				if(diff == 0)
					level = UP_TO_DATE;
				else if(diff < 0)
				{
					level = AHEAD;
					if(lat != null)
					{
						ComparableVersion latest = new ComparableVersion(lat);
						if(current.compareTo(latest) < 0)
							level = OUTDATED;
					}
				} else
					level = OUTDATED;
				
				liveURL = null;
				liveTitle = null;
				
				JSONObject dev = o.optJSONObject("dev");
				if(dev != null && dev.getBoolean("live"))
				{
					liveURL = dev.getString("url");
					
					// Get the livestream title
					String txt = HttpRequest.get(liveURL).body();
					txt = txt.substring(txt.indexOf("<title>") + 7);
					txt = txt.substring(0, txt.indexOf("</title>"));
					if(txt.toLowerCase().endsWith(" - youtube"))
						txt = txt.substring(0, txt.length() - 10);
					if(txt.toLowerCase().endsWith(" - twitch"))
						txt = txt.substring(0, txt.length() - 9);
					liveTitle = txt;
				}
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
			reloadThread = null;
		});
	}
	
	public void joinReload()
	{
		if(reloadThread != null)
			try
			{
				reloadThread.join();
			} catch(Exception ignored)
			{
			}
	}
	
	@Override
	public boolean isVisible(PlayerSkillData data)
	{
		return level == OUTDATED;
	}
}