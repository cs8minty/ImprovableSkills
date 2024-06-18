package org.zeith.improvableskills.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.improvableskills.proxy.ASMProxy;

/**
 * This mixin is non-functional because Mixins deny modifications of interfaces.
 * Even static methods. Hopefully this will get resolved eventually, because for now we have to use an ugly JS coremod.
 */
@Mixin(VibrationSystem.Ticker.class)
public class VibrationSystemTickerMixin
{
	@Inject(
			method = "receiveVibration",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void IS3_receiveVibration(ServerLevel p_282967_, VibrationSystem.Data p_283447_, VibrationSystem.User p_282301_, VibrationInfo p_281498_, CallbackInfoReturnable<Boolean> cir)
	{
		if(ASMProxy.cancelVibrationReception(p_282967_, p_283447_, p_282301_, p_281498_))
			cir.setReturnValue(true);
	}
}