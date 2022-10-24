package org.zeith.improvableskills.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.data.PlayerDataManager;

import java.math.BigInteger;

@MainThreaded
public class PacketDrawXP
		implements IPacket
{
	public int xp;
	
	public PacketDrawXP(int xp)
	{
		this.xp = xp;
	}
	
	public PacketDrawXP()
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
			BigInteger bi = data.storageXp.min(new BigInteger(Integer.toUnsignedString(this.xp)));
			int xp = Math.max(bi.intValue(), 0);
			XPUtil.setPlayersExpTo(player, cxp + xp);
			data.storageXp = data.storageXp.subtract(new BigInteger(Integer.toUnsignedString(xp)));
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