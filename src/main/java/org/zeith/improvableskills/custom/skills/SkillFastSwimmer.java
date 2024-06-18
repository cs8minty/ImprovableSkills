package org.zeith.improvableskills.custom.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillFastSwimmer
		extends PlayerSkillBase
{
	public static final ResourceLocation SWIM_ID = ImprovableSkills.id("fast_swimmer");
	
	public SkillFastSwimmer()
	{
		super(25);
		xpCalculator.xpValue = 2;
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		if(data.atTickRate(10) && !data.player.level().isClientSide)
		{
			AttributeInstance hp = data.player.getAttribute(NeoForgeMod.SWIM_SPEED);
			
			AttributeModifier mod = hp.getModifier(SWIM_ID);
			
			double val = isActive ? data.getSkillProgress(this) * 1.75F : 0;
			
			if(mod == null || mod.amount() != val)
			{
				if(mod != null) hp.removeModifier(SWIM_ID);
				if(val > 0)
					hp.addPermanentModifier(new AttributeModifier(SWIM_ID, val, AttributeModifier.Operation.ADD_VALUE));
			}
		}
	}
}