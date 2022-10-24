package org.zeith.improvableskills.api.evt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class VibrationEvent
		extends Event
{
	private final VibrationListener listener;
	private final ServerLevel level;
	private final GameEvent event;
	private final GameEvent.Context context;
	private final Vec3 from;
	private final Vec3 to;
	
	public VibrationEvent(VibrationListener listener, ServerLevel level, GameEvent event, GameEvent.Context context, Vec3 from, Vec3 to)
	{
		this.listener = listener;
		this.level = level;
		this.event = event;
		this.context = context;
		this.from = from;
		this.to = to;
	}
	
	public VibrationListener getListener()
	{
		return listener;
	}
	
	public ServerLevel getLevel()
	{
		return level;
	}
	
	public GameEvent getEvent()
	{
		return event;
	}
	
	public Vec3 getFrom()
	{
		return from;
	}
	
	public Vec3 getTo()
	{
		return to;
	}
	
	public double getDistance()
	{
		return from.distanceTo(to);
	}
	
	public GameEvent.Context getContext()
	{
		return context;
	}
}