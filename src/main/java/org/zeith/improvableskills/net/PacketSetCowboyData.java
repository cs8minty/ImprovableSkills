package org.zeith.improvableskills.net;

import net.minecraft.nbt.CompoundTag;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketSetCowboyData
		implements INBTPacket
{
	public Boolean enabled;
	
	public PacketSetCowboyData(Boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public PacketSetCowboyData()
	{
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		if(enabled != null) nbt.putBoolean("Enabled", enabled);
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		if(nbt.contains("Enabled")) enabled = nbt.getBoolean("Enabled");
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		PlayerDataManager.handleDataSafely(ctx.getSender(), data ->
		{
			if(enabled != null) data.cowboy = enabled;
		});
	}
}