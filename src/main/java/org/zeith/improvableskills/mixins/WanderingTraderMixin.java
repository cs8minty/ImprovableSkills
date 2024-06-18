package org.zeith.improvableskills.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.improvableskills.api.evt.ApplySpecialPricesEvent;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin
		extends AbstractVillager
{
	public WanderingTraderMixin(EntityType<? extends AbstractVillager> type, Level level)
	{
		super(type, level);
	}
	
	@Inject(
			method = "mobInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/WanderingTrader;setTradingPlayer(Lnet/minecraft/world/entity/player/Player;)V")
	)
	private void mobInteract_IS3(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
		for(MerchantOffer offer : getOffers())
		{
			offer.resetSpecialPriceDiff();
		}
		
		NeoForge.EVENT_BUS.post(new ApplySpecialPricesEvent(this, player));
	}
}