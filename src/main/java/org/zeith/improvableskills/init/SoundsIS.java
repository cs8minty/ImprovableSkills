package org.zeith.improvableskills.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.ImprovableSkills;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public interface SoundsIS
{
	SoundEvent PAGE_TURNS = SoundEvent.createVariableRangeEvent(ImprovableSkills.id("page_turns"));
	SoundEvent TREASURE_FOUND = SoundEvent.createVariableRangeEvent(ImprovableSkills.id("treasure_found"));
	SoundEvent CONNECT = SoundEvent.createVariableRangeEvent(ImprovableSkills.id("connect"));
	
	@SimplyRegister
	static void register(BiConsumer<ResourceLocation, SoundEvent> r)
	{
		for(Field f : SoundsIS.class.getDeclaredFields())
			if(SoundEvent.class.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()))
			{
				f.setAccessible(true);
				try
				{
					SoundEvent se = (SoundEvent) f.get(null);
					r.accept(se.getLocation(), se);
				} catch(ReflectiveOperationException e)
				{
					e.printStackTrace();
				}
			}
	}
}