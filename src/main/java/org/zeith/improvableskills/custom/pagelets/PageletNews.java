package org.zeith.improvableskills.custom.pagelets;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLLoader;
import org.zeith.hammerlib.util.ZeithLinkRepository;
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
	public final ResourceLocation texture = ImprovableSkills.id("textures/gui/news.png");
	
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
	
	@Getter
	String changes;
	
	@Override
	public void reload()
	{
		popping = false;
		
		try
		{
			var url = ZeithLinkRepository.findLink("mods/improvableskills/news").orElseThrow();
			
			this.changes = new String(
					HttpRequest.get(url)
							.userAgent("ImprovableSkills v" + ModHelper.getModVersion(ImprovableSkills.MOD_ID) + "; Minecraft v" + FMLLoader.versionInfo().mcVersion())
							.connectTimeout(30000)
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