package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.UUID;

public class SkillGenericProtection
		extends PlayerSkillBase
{
	public static final UUID PROTECTION_ID = UUID.fromString("8e56f8a6-a695-42d5-899b-89605f38cf80");
	
	public SkillGenericProtection()
	{
		super(20);
		lockedWithScroll = true;
		generateScroll = true;
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.SIMPLE_DUNGEON);
		setColor(0x4FFFDE);
		xpCalculator.setBaseFormula("%lvl%^2.75");
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		if(data.atTickRate(10))
		{
			AttributeInstance armor = data.player.getAttribute(Attributes.ARMOR);
			if(armor != null)
			{
				armor.removeModifier(PROTECTION_ID);
				armor.addPermanentModifier(new AttributeModifier(PROTECTION_ID, "IS3 Protection", data.getSkillLevel(this), AttributeModifier.Operation.ADDITION));
			}
		}
	}
}