package org.zeith.improvableskills.mixins;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandBlockEntityAccessor
{
	@Accessor
	int getBrewTime();
	
	@Accessor
	void setBrewTime(int brewTime);
}
