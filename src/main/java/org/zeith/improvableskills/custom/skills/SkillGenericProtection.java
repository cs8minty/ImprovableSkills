package org.zeith.improvableskills.custom.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillGenericProtection
		extends PlayerSkillBase
{
	public static final ResourceLocation PROTECTION_ID = ImprovableSkills.id("protection_skill");
	
	public SkillGenericProtection()
	{
		super(20);
		setupScroll();
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.SIMPLE_DUNGEON);
		setColor(0x4FFFDE);
		xpCalculator.setBaseFormula("%lvl%^2.75");
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		if(data.atTickRate(10))
		{
			AttributeInstance armor = data.player.getAttribute(Attributes.ARMOR);
			if(armor != null)
			{
				armor.removeModifier(PROTECTION_ID);
				if(isActive)
					armor.addPermanentModifier(new AttributeModifier(PROTECTION_ID, data.getSkillLevel(this), AttributeModifier.Operation.ADD_VALUE));
			}
		}
	}
}