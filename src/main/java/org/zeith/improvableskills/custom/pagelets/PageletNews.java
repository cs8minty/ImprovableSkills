package org.zeith.improvableskills.custom.pagelets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import org.zeith.hammerlib.util.java.Hashers;
import org.zeith.hammerlib.util.java.net.HttpRequest;
import org.zeith.hammerlib.util.mcf.ModHelper;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.GuiNewsBook;
import org.zeith.improvableskills.data.ClientData;

import java.nio.charset.StandardCharsets;

public class PageletNews
		extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/news.png");
	
	{
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":news"));
	}
	
	@Override
	public boolean isRight()
	{
		return false;
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
	
	boolean popping = true;
	String changes;
	
	public String getChanges()
	{
		return changes;
	}
	
	@Override
	public void reload()
	{
		popping = false;
		
		try
		{
			this.changes = new String(
					HttpRequest.get("https://mods.zeith.org/improvableskills/news.txt?mc=" + FMLLoader.versionInfo().mcVersion())
							.userAgent("ImprovableSkills v" + ModHelper.getModVersion(ImprovableSkills.MOD_ID))
							.connectTimeout(5000)
							.bytes(),
					StandardCharsets.UTF_8
			).replace("\r", "");
			
			String rem = Hashers.SHA256.hashify(changes);
			
			if(!ClientData.readData("news.sha").map(rem::equals).orElse(false))
				popping = true;
		} catch(Exception ignored)
		{
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesPop()
	{
		return popping;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiNewsBook createTab(PlayerSkillData data)
	{
		return new GuiNewsBook(this);
	}
}