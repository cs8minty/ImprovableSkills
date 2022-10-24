package org.zeith.improvableskills.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.data.PlayerDataManager;

import java.math.BigInteger;

@MainThreaded
public class PacketStoreXP
		implements IPacket
{
	public int xp;
	
	public PacketStoreXP(int xp)
	{
		this.xp = xp;
	}
	
	public PacketStoreXP()
	{
	}
	
	@Override
	public void serverExecute(PacketContext net)
	{
		ServerPlayer player = net.getSender();
		
		PlayerDataManager.handleDataSafely(player, data ->
		{
			if(!data.enableXPBank) return;
			int cxp = XPUtil.getXPTotal(player);
			int xp = Math.min(this.xp, cxp);
			XPUtil.setPlayersExpTo(player, cxp - xp);
			data.storageXp = data.storageXp.add(new BigInteger(Integer.toUnsignedString(xp)));
			data.sync();
		});
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeInt(xp);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		xp = buf.readInt();
	}
}