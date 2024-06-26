package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.zeith.improvableskills.api.DamageSourceProcessor;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillAtkDmgMelee
		extends PlayerSkillBase
{
	public SkillAtkDmgMelee()
	{
		super(15);
		xpCalculator.xpValue = 3;
		addListener(this::damageHook);
	}
	
	private void damageHook(LivingIncomingDamageEvent e)
	{
		DamageSource ds = e.getSource();
		if(DamageSourceProcessor.getDamageType(ds) != DamageSourceProcessor.DamageType.MELEE) return;
		PlayerDataManager.handleDataSafely(DamageSourceProcessor.getMeleeAttacker(ds), data ->
		{
			if(!data.isSkillActive(this)) return;
			float pp = data.getSkillProgress(this);
			e.setAmount(e.getAmount() + (e.getAmount() * pp / 2F) + pp * 7F);
		});
	}
}