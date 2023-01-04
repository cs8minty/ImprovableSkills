package org.zeith.improvableskills.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.ImprovableSkills;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public class SoundsIS
{
	public static final SoundEvent PAGE_TURNS = SoundEvent.createVariableRangeEvent(new ResourceLocation(ImprovableSkills.MOD_ID, "page_turns"));
	public static final SoundEvent TREASURE_FOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation(ImprovableSkills.MOD_ID, "treasure_found"));
	public static final SoundEvent CONNECT = SoundEvent.createVariableRangeEvent(new ResourceLocation(ImprovableSkills.MOD_ID, "connect"));
	
	@SimplyRegister
	public static void register(BiConsumer<ResourceLocation, SoundEvent> r)
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