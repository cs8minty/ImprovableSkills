package org.zeith.improvableskills.custom.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillHealth
		extends PlayerSkillBase
{
	public static final ResourceLocation HP_ID = ImprovableSkills.id("extra_health");
	
	public SkillHealth()
	{
		super(20);
		setupScroll();
		xpCalculator.xpValue = 3;
		setColor(0xFF3535);
		getLoot().chance.n = 9;
		getLoot().setLootTable(BuiltInLootTables.END_CITY_TREASURE);
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		if(data.atTickRate(10))
		{
			AttributeInstance hp = data.player.getAttribute(Attributes.MAX_HEALTH);
			
			AttributeModifier mod = hp.getModifier(HP_ID);
			
			double val = isActive ? data.getSkillLevel(this) : 0;
			
			if(mod == null || mod.amount() != val)
			{
				if(mod != null) hp.removeModifier(HP_ID);
				if(val > 0)
					hp.addPermanentModifier(new AttributeModifier(HP_ID, val, AttributeModifier.Operation.ADD_VALUE));
			}
			
			if(data.player.getHealth() > hp.getValue()) data.player.setHealth(data.player.getHealth());
		}
	}
}