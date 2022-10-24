package org.zeith.improvableskills.custom.skills;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.UUID;

public class SkillFastSwimmer
		extends PlayerSkillBase
{
	public static final UUID SWIM_ID = UUID.fromString("856d21d3-0086-4ccd-bdb0-a43d98f5104c");
	
	public SkillFastSwimmer()
	{
		super(25);
		xpCalculator.xpValue = 2;
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		if(data.atTickRate(10) && !data.player.level.isClientSide)
		{
			AttributeInstance hp = data.player.getAttribute(ForgeMod.SWIM_SPEED.get());
			
			AttributeModifier mod = hp.getModifier(SWIM_ID);
			
			double val = data.getSkillLevel(this) * 1.75F / maxLvl;
			
			if(mod == null || mod.getAmount() != val)
			{
				if(mod != null) hp.removeModifier(SWIM_ID);
				hp.addPermanentModifier(new AttributeModifier(SWIM_ID, "IS3 Swim Speed", val, AttributeModifier.Operation.ADDITION));
			}
		}
	}
}