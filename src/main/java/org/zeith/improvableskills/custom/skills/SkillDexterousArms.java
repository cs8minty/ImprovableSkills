package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

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
	}
}