package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillObsidianSkin
		extends PlayerSkillBase
{
	public SkillObsidianSkin()
	{
		super(20);
		setupScroll();
		getLoot().chance.n = 3;
		getLoot().setLootTable(BuiltInLootTables.NETHER_BRIDGE);
		setColor(0x9B3EC9);
		xpCalculator.xpValue = 2;
		addListener(this::damageHook);
	}
	
	private void damageHook(LivingIncomingDamageEvent e)
	{
		DamageSource ds = e.getSource();
		if(ds.type().effects() == DamageEffects.BURNING && e.getEntity() instanceof Player p)
			PlayerDataManager.handleDataSafely(p, data ->
			{
				if(!data.isSkillActive(this)) return;
				e.setAmount(e.getAmount() * (1F - data.getSkillProgress(this) * 0.5F));
			});
	}
}