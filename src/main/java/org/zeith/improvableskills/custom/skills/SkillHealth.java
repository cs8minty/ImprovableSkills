package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.UUID;

public class SkillHealth
		extends PlayerSkillBase
{
	public static final UUID HP_ID = UUID.fromString("a6c5d900-a39b-4e1f-9572-f48e174335f2");
	
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
	public void tick(PlayerSkillData data)
	{
		if(data.atTickRate(10))
		{
			AttributeInstance hp = data.player.getAttribute(Attributes.MAX_HEALTH);
			
			AttributeModifier mod = hp.getModifier(HP_ID);
			
			double val = data.getSkillLevel(this);
			
			if(mod == null || mod.getAmount() != val)
			{
				if(mod != null) hp.removeModifier(HP_ID);
				hp.addPermanentModifier(new AttributeModifier(HP_ID, "IS3 Health", val, AttributeModifier.Operation.ADDITION));
			}
			
			if(data.player.getHealth() > hp.getValue()) data.player.setHealth(data.player.getHealth());
		}
	}
}