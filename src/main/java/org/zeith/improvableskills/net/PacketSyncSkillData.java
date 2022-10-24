package org.zeith.improvableskills.net;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.data.PlayerDataManager;

public class PacketSyncSkillData
		implements INBTPacket
{
	public CompoundTag nbt;
	
	public static void sync(ServerPlayer mp)
	{
		try
		{
			if(mp != null)
				PlayerDataManager.handleDataSafely(mp, data -> Network.sendTo(new PacketSyncSkillData(data), mp));
		} catch(NullPointerException npe)
		{
			// networking issues, pretty unsure how to prevent.
		}
	}
	
	private PacketSyncSkillData(PlayerSkillData data)
	{
		nbt = data.serializeNBT();
		nbt.putInt("PlayerLocalXP", XPUtil.getXPTotal(data.player));
	}
	
	public PacketSyncSkillData()
	{
		nbt = new CompoundTag();
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		sync(ctx.getSender());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext net)
	{
		IGuiSkillDataConsumer c = Cast.cast(Minecraft.getInstance().screen, IGuiSkillDataConsumer.class);
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getInstance().player, nbt);
		if(c != null)
			c.applySkillData(SyncSkills.CLIENT_DATA);
		Player player = Minecraft.getInstance().player;
		
		// Prevent console pollution
		if(player == null)
			return;
		
		XPUtil.setPlayersExpTo(player, nbt.getInt("PlayerLocalXP"));
		// This is not REQUIRED but preffered for mods that may use this tag
		player.getPersistentData().put(ImprovableSkills.NBT_DATA_TAG, nbt);
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		nbt.merge(this.nbt);
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		this.nbt = nbt;
	}
}