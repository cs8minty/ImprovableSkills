package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import org.zeith.improvableskills.api.DamageSourceProcessor;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillAtkDmgRanged
		extends PlayerSkillBase
{
	public SkillAtkDmgRanged()
	{
		super(15);
		setupScroll();
		getLoot().chance.n = 40;
		getLoot().setLootTable(EntityType.SKELETON.getDefaultLootTable());
		xpCalculator.xpValue = 3;
		addListener(this::damageHook);
	}
	
	private void damageHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		if(ds != null && DamageSourceProcessor.getDamageType(ds) == DamageSourceProcessor.DamageType.RANGED)
		{
			Player p = DamageSourceProcessor.getRangedOwner(ds);
			PlayerDataManager.handleDataSafely(p, data ->
			{
				int melee = data.getSkillLevel(this);
				float pp = data.getSkillProgress(this);
				e.setAmount(e.getAmount() + (e.getAmount() * pp) + melee / 2F);
			});
		}
	}
}