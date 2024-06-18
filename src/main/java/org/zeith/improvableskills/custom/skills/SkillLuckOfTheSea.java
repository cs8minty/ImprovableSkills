package org.zeith.improvableskills.custom.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.UUID;

public class SkillLuckOfTheSea
		extends PlayerSkillBase
{
	public static final ResourceLocation LOTS_LUCK = ImprovableSkills.id("luck_of_the_sea");
	
	public SkillLuckOfTheSea()
	{
		super(15);
		setupScroll();
		getLoot().chance.n = 10;
		getLoot().setLootTable(BuiltInLootTables.FISHING);
		getLoot().exclusive = true;
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
				luck.addPermanentModifier(new AttributeModifier(LOTS_LUCK, level * 2D, AttributeModifier.Operation.ADD_VALUE));
		}
	}
}