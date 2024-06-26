package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillPVP
		extends PlayerSkillBase
{
	public SkillPVP()
	{
		super(20);
		xpCalculator.xpValue = 2;
		addListener(this::damageHook);
	}
	
	private void damageHook(LivingIncomingDamageEvent e)
	{
		if(e.getSource().getEntity() instanceof Player && e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(!data.isSkillActive(this)) return;
				float pp = 1F - data.getSkillProgress(this);
				e.setAmount(e.getAmount() * Math.min(1F, 0.75F + pp / 4F));
			});
	}
}