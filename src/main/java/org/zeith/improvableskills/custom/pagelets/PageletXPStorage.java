package org.zeith.improvableskills.custom.pagelets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.GuiXPBank;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;

public class PageletXPStorage
		extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/xp_bank.png");
	
	{
		setTitle(Component.translatable("pagelet." + ImprovableSkills.MOD_ID + ":xp_bank"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Object getIcon()
	{
		Object o = super.getIcon();
		if(!(o instanceof UV)) setIcon(o = new UV(texture, 0, 0, 256, 256));
		return o;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return new GuiXPBank(this);
	}
	
	@Override
	public void reload()
	{
	}
	
	@Override
	public boolean isVisible(PlayerSkillData data)
	{
		return data.enableXPBank;
	}
}