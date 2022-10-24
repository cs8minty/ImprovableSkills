package org.zeith.improvableskills.api.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;

public record AbilityTooltip(PlayerAbilityBase ability)
		implements TooltipComponent
{
}