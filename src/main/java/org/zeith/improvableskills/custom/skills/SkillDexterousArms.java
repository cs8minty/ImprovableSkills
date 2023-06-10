package org.zeith.improvableskills.custom.skills;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.evt.DamageItemEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

import java.util.Random;

public class SkillDexterousArms
		extends PlayerSkillBase
{
	public SkillDexterousArms()
	{
		super(15);
		setupScroll();
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.ABANDONED_MINESHAFT);
		setColor(0xFFC031);
		xpCalculator.xpValue = 3;
		xpCalculator.setBaseFormula("((%lvl%+1)^%xpv%)/2");
		addListener(this::hook);
	}
	
	private final Random rng = new Random();
	
	private void hook(DamageItemEvent e)
	{
		if(e.getEntity() instanceof Player player)
		{
			PlayerDataManager.handleDataSafely(player, data ->
			{
				if(!data.isSkillActive(this))
					return;
				
				var chanceToSaveDurability = Mth.lerp(
						data.getSkillProgress(this),
						0,
						60
				);
				
				for(int i = 0; i < e.getNewDamage(); ++i)
					if(rng.nextInt(100) + 1 < chanceToSaveDurability)
						e.setNewDamage(e.getNewDamage() - 1);
			});
		}
	}
}