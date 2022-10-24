package org.zeith.improvableskills.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.Containers;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.improvableskills.api.evt.HarvestDropsEvent;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin
{
	@Shadow
	protected ServerLevel level;
	
	@Shadow
	@Final
	protected ServerPlayer player;
	
	@Inject(
			method = "removeBlock",
			at = @At("HEAD"),
			remap = false
	)
	public void removeBlock_IS3(BlockPos p_180235_1_, boolean canHarvest, CallbackInfoReturnable<Boolean> cir)
	{
		var drops = new HarvestDropsEvent(level, p_180235_1_, level.getBlockState(p_180235_1_), player);
		MinecraftForge.EVENT_BUS.post(drops);
		Containers.dropContents(level, p_180235_1_, drops.getDrops());
	}
}