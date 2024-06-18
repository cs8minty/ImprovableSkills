package org.zeith.improvableskills.custom.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.evt.CowboyStartEvent;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.TagsIS3;
import org.zeith.improvableskills.net.PacketSetCowboyData;

public class AbilityCowboy
		extends PlayerAbilityBase
		implements IRegisterListener
{
	public AbilityCowboy()
	{
		setColor(0xD19300);
	}
	
	@Override
	public void onPostRegistered()
	{
		super.onPostRegistered();
		NeoForge.EVENT_BUS.addListener(this::entityClick);
	}
	
	public void entityClick(PlayerInteractEvent.EntityInteract e)
	{
		if(!(e.getTarget() instanceof LivingEntity le) || le.isDeadOrDying())
			return;
		
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			CowboyStartEvent evt;
			if(data.cowboy && !(NeoForge.EVENT_BUS.post(evt = new CowboyStartEvent(data.player, le))).isCanceled())
			{
				if(evt.getResult() == CowboyStartEvent.Result.DENY) return; // deny this action
				if(evt.getResult() == CowboyStartEvent.Result.DEFAULT)
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