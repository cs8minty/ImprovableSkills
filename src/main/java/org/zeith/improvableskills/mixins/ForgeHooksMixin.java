package org.zeith.improvableskills.mixins;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zeith.improvableskills.custom.LootTableLoader;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin
{
	@ModifyVariable(
			method = "loadLootTable",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;freeze()V"),
			index = 5
	)
	private static LootTable loadLootTable_ImprovableSkills(LootTable table)
	{
		LootTableLoader.loadTable(table);
		return table;
	}
}