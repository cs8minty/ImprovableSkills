package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.UUID;

public class SkillLuckOfTheSea
		extends PlayerSkillBase
{
	public static final UUID LOTS_LUCK = UUID.fromString("d489061e-0b53-4aa3-a7f4-f1a9a726ef49");
	
	public SkillLuckOfTheSea()
	{
		super(15);
		setupScroll();
		getLoot().chance.n = 10;
		getLoot().setLootTable(BuiltInLootTables.FISHING);
		setColor(0x4CC8E8);
		xpCalculator.xpValue = 2;
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		var player = data.player;
		var hook = player.fishing;
		int level = data.getSkillLevel(this);
		AttributeInstance luck = player.getAttributes().getInstance(Attributes.LUCK);
		if(luck != null)
		{
			luck.removeModifier(LOTS_LUCK);
			if(isActive && hook != null && !hook.isRemoved())
				luck.addPermanentModifier(new AttributeModifier(LOTS_LUCK, "IS3 Fishing Luck", level * 2D, AttributeModifier.Operation.ADDITION));
		}
	}
}