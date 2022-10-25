package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
	
	private void damageHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		if(ds != null && e.getSource().getEntity() instanceof Player attacker && e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(!data.isSkillActive(this)) return;
				float pp = 1F - data.getSkillProgress(this);
				e.setAmount(e.getAmount() * Math.min(1F, 0.75F + pp / 4F));
			});
	}
}