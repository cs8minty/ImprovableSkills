package org.zeith.improvableskills.custom.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.evt.CowboyStartEvent;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.TagsIS3;
import org.zeith.improvableskills.net.PacketSetCowboyData;

public class AbilityCowboy
		extends PlayerAbilityBase
{
	public AbilityCowboy()
	{
		setColor(0xD19300);
		MinecraftForge.EVENT_BUS.addListener(this::entityClick);
	}
	
	@SubscribeEvent
	public void entityClick(PlayerInteractEvent.EntityInteract e)
	{
		if(!(e.getTarget() instanceof LivingEntity le))
			return;
		
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			CowboyStartEvent evt;
			if(data.cowboy && !(MinecraftForge.EVENT_BUS.post(evt = new CowboyStartEvent(data.player, le))))
			{
				if(evt.getResult() == Event.Result.DENY) return; // deny this action
				if(evt.getResult() == Event.Result.DEFAULT)
				{
					if(le.getType().is(Tags.EntityTypes.BOSSES))
						return; // prevent bosses
					if(le.getType().is(TagsIS3.EntityTypes.PREVENT_COWBOY_INTERACTION))
						return; // prevent our entity type tag for easy way of blocking this behavior
				}
				
				// Perform the sit:
				if(!data.player.level().isClientSide())
					data.player.startRiding(le);
				e.setCanceled(true);
			}
		});
	}
	
	@Override
	public void onUnlocked(PlayerSkillData data)
	{
		data.cowboy = true;
	}
	
	@Override
	public boolean showDisabledIcon(PlayerSkillData data)
	{
		return !data.cowboy;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		PlayerDataManager.handleDataSafely(player, data ->
		{
			Network.sendToServer(new PacketSetCowboyData(data.cowboy = !data.cowboy));
		});
	}
}