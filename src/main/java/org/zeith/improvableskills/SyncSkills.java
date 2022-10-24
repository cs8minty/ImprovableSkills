package org.zeith.improvableskills;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.api.PlayerSkillData;

public class SyncSkills
{
	public static PlayerSkillData CLIENT_DATA;
	
	@OnlyIn(Dist.CLIENT)
	public static PlayerSkillData getData()
	{
		if(CLIENT_DATA == null || CLIENT_DATA.player != Minecraft.getInstance().player)
			return CLIENT_DATA = new PlayerSkillData(Minecraft.getInstance().player);
		return CLIENT_DATA;
	}
}