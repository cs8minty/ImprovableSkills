package org.zeith.improvableskills.api.evt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class VibrationEvent
		extends Event
{
	private final ServerLevel level;
	private final VibrationSystem.Data data;
	private final VibrationSystem.User user;
	private final VibrationInfo info;
	
	public VibrationEvent(ServerLevel level, VibrationSystem.Data data, VibrationSystem.User user, VibrationInfo info)
	{
		this.level = level;
		this.data = data;
		this.user = user;
		this.info = info;
	}
	
	public ServerLevel getLevel()
	{
		return level;
	}
	
	public VibrationSystem.Data getData()
	{
		return data;
	}
	
	public VibrationSystem.User getUser()
	{
		return user;
	}
	
	public VibrationInfo getInfo()
	{
		return info;
	}
}