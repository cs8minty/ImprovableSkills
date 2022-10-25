package org.zeith.improvableskills;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.net.PacketOpenSkillsBook;
import org.zeith.improvableskills.net.PacketSyncSkillData;

public class SyncSkills
{
	private static PlayerSkillData CLIENT_DATA;
	
	public static boolean is(PlayerSkillData data)
	{
		return data == CLIENT_DATA;
	}
	
	public static void doCheck(Player localPlayer)
	{
		if(localPlayer == null && CLIENT_DATA != null)
		{
			ImprovableSkills.LOG.info("Reset client skill data.");
			CLIENT_DATA = null;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static PlayerSkillData getData()
	{
		var mcp = Minecraft.getInstance().player;
		if(CLIENT_DATA == null || CLIENT_DATA.player != mcp)
		{
			CLIENT_DATA = new PlayerSkillData(mcp);
			CLIENT_DATA.requestSync();
		}
		return CLIENT_DATA;
	}
	
	public static void handle(Player localPlayer, PacketOpenSkillsBook packet)
	{
		CLIENT_DATA = PlayerSkillData.deserialize(localPlayer, packet.getNbt());
	}
	
	public static void handle(Player localPlayer, PacketSyncSkillData packet)
	{
		CLIENT_DATA = PlayerSkillData.deserialize(localPlayer, packet.getNbt());
	}
}