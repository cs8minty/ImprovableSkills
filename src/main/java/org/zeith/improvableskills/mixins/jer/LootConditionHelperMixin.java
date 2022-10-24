package org.zeith.improvableskills.mixins.jer;

import jeresources.api.drop.LootDrop;
import jeresources.api.util.LootConditionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.compat.jer.ConditionalComponent;
import org.zeith.improvableskills.utils.loot.LootConditionSkillScroll;

@Mixin(value = LootConditionHelper.class, remap = false)
public class LootConditionHelperMixin
{
	@Inject(
			method = "applyCondition",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void applyCondition_IS3(LootItemCondition condition, LootDrop lootDrop, CallbackInfo ci)
	{
		if(condition instanceof LootConditionSkillScroll cond)
		{
			lootDrop.addConditional(
					new ConditionalComponent(
							Component.translatable(ImprovableSkills.MOD_ID + ".jer.skill_condition",
											cond.getContextComponent())
									.withStyle(ChatFormatting.AQUA)
					)
			);
			ci.cancel();
		}
	}
}