package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillSoftLanding
		extends PlayerSkillBase
{
	public SkillSoftLanding()
	{
		super(10);
		xpCalculator.xpValue = 2;
		addListener(this::hook);
		addListener(this::damageHook);
	}
	
	private void damageHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		if(ds != null && ds.isFall() && e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(data.isSkillActive(this) && data.getSkillLevel(this) >= getMaxLevel() && e.getAmount() >= p.getHealth())
					e.setAmount(p.getHealth() - 1F);
			});
	}
	
	private void hook(LivingFallEvent e)
	{
		if(e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(data == null || !data.isSkillActive(this))
					return;
				float softLandingStatLevel = data.getSkillProgress(this);
				float reduce = Math.min(0.5F, Math.max(0.25F, softLandingStatLevel));
				reduce = 1.0F - reduce;
				if(softLandingStatLevel > 0)
				{
					e.setDistance(e.getDistance() * reduce);
					p.fallDistance *= reduce;
					e.setDamageMultiplier(e.getDamageMultiplier() * reduce);
				}
			});
	}
}