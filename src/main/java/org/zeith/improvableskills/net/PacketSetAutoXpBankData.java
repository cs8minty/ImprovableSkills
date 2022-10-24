package org.zeith.improvableskills.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketSetAutoXpBankData
		implements INBTPacket
{
	public Integer threshold;
	public Boolean enabled;
	
	public PacketSetAutoXpBankData(Integer threshold, Boolean enabled)
	{
		this.threshold = threshold;
		this.enabled = enabled;
	}
	
	public PacketSetAutoXpBankData(Integer threshold)
	{
		this.threshold = threshold;
	}
	
	public PacketSetAutoXpBankData(Boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public PacketSetAutoXpBankData()
	{
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		if(threshold != null) nbt.putInt("Threshold", threshold);
		if(enabled != null) nbt.putBoolean("Enabled", enabled);
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		if(nbt.contains("Threshold")) threshold = nbt.getInt("Threshold");
		if(nbt.contains("Enabled")) enabled = nbt.getBoolean("Enabled");
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		PlayerDataManager.handleDataSafely(ctx.getSender(), data ->
		{
			if(enabled != null) data.autoXpBank = enabled;
			if(threshold != null) data.autoXpBankThreshold = Mth.clamp(threshold, 0, Integer.MAX_VALUE);
		});
	}
}