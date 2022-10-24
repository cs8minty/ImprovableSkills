package org.zeith.improvableskills.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.api.evt.VibrationEvent;

@Mixin(VibrationListener.class)
public class VibrationListenerMixin
{
	@Inject(
			method = "scheduleSignal",
			at = @At("HEAD"),
			cancellable = true
	)
	public void scheduleSignal_IS3(ServerLevel level, GameEvent event, GameEvent.Context context, Vec3 from, Vec3 to, CallbackInfo ci)
	{
		if(MinecraftForge.EVENT_BUS.post(new VibrationEvent(Cast.cast(this), level, event, context, from, to)))
			ci.cancel();
	}
}