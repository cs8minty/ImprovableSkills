package org.zeith.improvableskills.net;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.IGuiSkillDataConsumer;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.data.PlayerDataManager;

@Getter
public class PacketSyncSkillData
		implements IPacket
{
	private CompoundTag nbt;
	
	public static void sync(ServerPlayer mp)
	{
		try
		{
			if(mp != null)
				PlayerDataManager.handleDataSafely(mp, data -> Network.sendTo(new PacketSyncSkillData(mp.registryAccess(), data), mp));
		} catch(NullPointerException npe)
		{
			// networking issues, pretty unsure how to prevent.
		}
	}
	
	private PacketSyncSkillData(RegistryAccess access, PlayerSkillData data)
	{
		nbt = data.serializeNBT(access);
		nbt.putInt("PlayerLocalXPLevel", data.player.experienceLevel);
		nbt.putFloat("PlayerLocalXPProgress", data.player.experienceProgress);
		nbt.putFloat("PlayerLocalHealth", data.player.getHealth());
	}
	
	public PacketSyncSkillData()
	{
		this.nbt = new CompoundTag();
	}
	
	@Override
	public boolean executeOnMainThread()
	{
		return LogicalSidePredictor.getCurrentLogicalSide().isClient();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext net)
	{
		Player player = Minecraft.getInstance().player;
		
		// Prevent console pollution
		if(player == null) return;
		
		SyncSkills.handle(player, this);
		
		Cast.optionally(Minecraft.getInstance().screen, IGuiSkillDataConsumer.class)
				.ifPresent(c -> c.applySkillData(SyncSkills.getData()));
		
		if(nbt.contains("PlayerLocalXPLevel"))
			player.experienceLevel = nbt.getInt("PlayerLocalXPLevel");
		if(nbt.contains("PlayerLocalXPProgress"))
			player.experienceProgress = nbt.getFloat("PlayerLocalXPProgress");
		if(nbt.contains("PlayerLocalHealth"))
			player.setHealth(nbt.getFloat("PlayerLocalHealth"));
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		PlayerDataManager.handleDataSafely(ctx.getSender(), data ->
				ctx.withReply(new PacketSyncSkillData(ctx.registryAccess(), data))
		);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeNbt(nbt);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.nbt = buf.readNbt();
	}
}