package org.zeith.improvableskills.api.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public record SkillTooltip(PlayerSkillBase skill)
		implements TooltipComponent
{
}