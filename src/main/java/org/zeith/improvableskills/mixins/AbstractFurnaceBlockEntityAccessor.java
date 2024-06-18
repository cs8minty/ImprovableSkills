package org.zeith.improvableskills.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor
{
	@Invoker
	static boolean callBurn(RegistryAccess access, @Nullable RecipeHolder<?> holder, NonNullList<ItemStack> items, int i, AbstractFurnaceBlockEntity furnace) {throw new UnsupportedOperationException();}
	
	@Accessor
	int getLitTime();
	
	@Accessor
	void setLitTime(int litTime);
	
	@Accessor
	int getCookingProgress();
	
	@Accessor
	void setCookingProgress(int cookingProgress);
	
	@Accessor
	int getCookingTotalTime();
	
	@Accessor
	RecipeType<? extends AbstractCookingRecipe> getRecipeType();
	
	@Accessor
	NonNullList<ItemStack> getItems();
	
	@Accessor
	RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> getQuickCheck();
}
