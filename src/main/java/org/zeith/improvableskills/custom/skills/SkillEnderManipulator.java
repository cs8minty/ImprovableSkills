package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillEnderManipulator
		extends PlayerSkillBase
{
	public SkillEnderManipulator()
	{
		super(5);
		setupScroll();
		getLoot().chance.n = 20;
		getLoot().setLootTable(EntityType.ENDERMAN.getDefaultLootTable());
		setColor(0xD5DA94);
		xpCalculator.xpValue = 3;
		addListener(this::hook);
	}
	
	private void hook(EntityTeleportEvent.EnderPearl e)
	{
		var p = e.getPlayer();
		
		if(p != null)
		{
			int lvl = PlayerDataManager.handleDataSafely(p, data -> data.isSkillActive(this) ? data.getSkillLevel(this) : 0, 0).intValue();
			
			if(lvl > 0)
			{
				float prog = lvl / (float) (getMaxLevel() - 1);
				if(prog > 1)
				{
					e.setAttackDamage(e.getAttackDamage() / 10F);
					p.heal(1);
				} else
					e.setAttackDamage(e.getAttackDamage() * (1F - prog * 0.8F));
			}
		}
	}
}