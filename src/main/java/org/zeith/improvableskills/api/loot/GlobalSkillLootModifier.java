package org.zeith.improvableskills.api.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.ItemsIS;

import java.util.List;

@SimplyRegister
public class GlobalSkillLootModifier
		extends LootModifier
{
	@RegistryName("exclusive_modifier")
	public static final Registrar<MapCodec<GlobalSkillLootModifier>> CODEC = Registrar.globalLootModifier(RecordCodecBuilder.mapCodec(inst ->
			codecStart(inst)
					.apply(inst, GlobalSkillLootModifier::new)
	));
	
	/**
	 * Constructs a LootModifier.
	 *
	 * @param conditionsIn
	 * 		the ILootConditions that need to be matched before the loot is modified.
	 */
	protected GlobalSkillLootModifier(LootItemCondition[] conditionsIn)
	{
		super(conditionsIn);
	}
	
	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		return generatedLoot.stream()
				.filter(stack -> stack.is(ItemsIS.SKILL_SCROLL))
				.filter(is ->
				{
					var skill = ItemSkillScroll.getSkillFromScroll(is);
					if(skill == null) return false;
					var loot = skill.getLoot();
					if(loot == null) return false;
					return loot.exclusive;
				})
				.findFirst()
				.map(lst -> new ObjectArrayList<>(List.of(lst)))
				.orElse(generatedLoot);
	}
	
	@Override
	public MapCodec<? extends IGlobalLootModifier> codec()
	{
		return CODEC.get();
	}
}