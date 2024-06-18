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
		super(3);
		setupScroll();
		getLoot().chance.n = 7;
		getLoot().setLootTable(BuiltInLootTables.PIGLIN_BARTERING);
		getLoot().exclusive = true;
		setColor(0x00FFFF);
		xpCalculator.setBaseFormula("(%lvl%+1)^6+150");
		addListener(this::hook);
	}
	
	private void hook(EntityEnchantmentLevelEvent e)
	{
		if(e.getEntity() instanceof Player player && e.enchantment().is(Enchantments.SOUL_SPEED))
			e.max(PlayerDataManager.handleDataSafely(player, data ->
							data.isSkillActive(this) ? Math.min(data.getSkillLevel(this), 3) : 0,
					0
			));
	}
}