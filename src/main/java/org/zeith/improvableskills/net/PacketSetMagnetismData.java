package org.zeith.improvableskills.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketSetMagnetismData
		implements INBTPacket
{
	public Float range;
	public Boolean enabled;
	
	public PacketSetMagnetismData(Float range, Boolean enabled)
	{
		this.range = range;
		this.enabled = enabled;
	}
	
	public PacketSetMagnetismData(Float range)
	{
		this.range = range;
	}
	
	public PacketSetMagnetismData(Boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public PacketSetMagnetismData()
	{
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		if(range != null) nbt.putFloat("Range", range);
		if(enabled != null) nbt.putBoolean("Enabled", enabled);
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		if(nbt.contains("Range")) range = nbt.getFloat("Range");
		if(nbt.contains("Enabled")) enabled = nbt.getBoolean("Enabled");
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		PlayerDataManager.handleDataSafely(ctx.getSender(), data ->
		{
			if(enabled != null) data.magnetism = enabled;
			if(range != null) data.magnetismRange = Mth.clamp(range, 0, 8);
		});
	}
}