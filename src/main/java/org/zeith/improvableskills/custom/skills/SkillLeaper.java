package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillLeaper
		extends PlayerSkillBase
{
	public SkillLeaper()
	{
		super(15);
		xpCalculator.xpValue = 2;
		addListener(this::hook);
	}
	
	private void hook(LivingEvent.LivingJumpEvent e)
	{
		if(e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(!data.isSkillActive(this)) return;
				var leaper = data.getSkillProgress(this);
				if(leaper > 0) p.setDeltaMovement(p.getDeltaMovement().multiply(1F, 1 + leaper * 0.75F, 1F));
			});
	}
}