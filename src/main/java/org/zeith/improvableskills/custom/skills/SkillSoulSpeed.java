package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.evt.EntityEnchantmentLevelEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillSoulSpeed
		extends PlayerSkillBase
{
	public SkillSoulSpeed()
	{
		super(Enchantments.SOUL_SPEED.getMaxLevel());
		setupScroll();
		getLoot().chance.n = 7;
		getLoot().setLootTable(BuiltInLootTables.PIGLIN_BARTERING);
		setColor(0x00FFFF);
		xpCalculator.setBaseFormula("(%lvl%+1)^6+150");
		addListener(this::hook);
	}
	
	private void hook(EntityEnchantmentLevelEvent e)
	{
		if(e.getEntity() instanceof Player player && e.getEnchantment() == Enchantments.SOUL_SPEED)
			e.max(PlayerDataManager.handleDataSafely(player, data ->
							data.isSkillActive(this) ? Math.min(data.getSkillLevel(this), Enchantments.SOUL_SPEED.getMaxLevel()) : 0,
					0
			));
	}
}