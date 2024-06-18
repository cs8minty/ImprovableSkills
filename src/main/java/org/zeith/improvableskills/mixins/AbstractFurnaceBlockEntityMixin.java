package org.zeith.improvableskills.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.api.evt.CalculateAdditionalFurnaceExperienceMultiplier;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin
{
	@Shadow
	@Final
	private Object2IntOpenHashMap<ResourceLocation> recipesUsed;
	
	@Shadow
	private static void createExperience(ServerLevel level, Vec3 p_155000_, int p_155001_, float p_155002_)
	{
	}
	
	@Inject(
			method = "awardUsedRecipesAndPopExperience",
			at = @At("HEAD")
	)
	public void awardUsedRecipesAndPopExperience_IS3(ServerPlayer player, CallbackInfo ci)
	{
		var level = player.serverLevel();
		var evt = new CalculateAdditionalFurnaceExperienceMultiplier(player, Cast.cast(this));
		NeoForge.EVENT_BUS.post(evt);
		float mul = evt.getMultiplier();
		if(mul > 0) for(var entry : this.recipesUsed.object2IntEntrySet())
			level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe ->
					createExperience(level, player.position(), entry.getIntValue(), ((AbstractCookingRecipe) recipe.value()).getExperience() * mul)
			);
	}
}