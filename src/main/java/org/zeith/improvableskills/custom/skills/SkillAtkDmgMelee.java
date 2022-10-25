package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
	
	private void damageHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		if(ds != null && DamageSourceProcessor.getDamageType(ds) == DamageSourceProcessor.DamageType.MELEE)
		{
			Player p = DamageSourceProcessor.getAlchemicalOwner(ds.getDirectEntity());
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(!data.isSkillActive(this)) return;
				float pp = data.getSkillProgress(this);
				e.setAmount(e.getAmount() + (e.getAmount() * pp / 2F) + pp * 7F);
			});
		}
	}
}