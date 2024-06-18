package org.zeith.improvableskills.mixins;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.improvableskills.api.evt.ApplySpecialPricesEvent;

@Mixin(Villager.class)
public abstract class VillagerMixin
		extends AbstractVillager
{
	public VillagerMixin(EntityType<? extends AbstractVillager> type, Level level)
	{
		super(type, level);
	}
	
	@Inject(
			method = "updateSpecialPrices",
			at = @At("HEAD")
	)
	private void updateSpecialPrices_IS3(Player forPlayer, CallbackInfo ci)
	{
		NeoForge.EVENT_BUS.post(new ApplySpecialPricesEvent(this, forPlayer));
	}
}