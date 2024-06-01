package org.zeith.improvableskills.api.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.custom.items.ItemSkillScroll;
import org.zeith.improvableskills.init.ItemsIS;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GlobalSkillLootModifier
		extends LootModifier
{
	public static final ResourceLocation EXCLUSIVE_SKILL_MODIFIERS = ImprovableSkills.id("exclusive_modifier");
	public static final Codec<GlobalSkillLootModifier> CODEC = RecordCodecBuilder.create(inst ->
			codecStart(inst)
					.apply(inst, GlobalSkillLootModifier::new)
	);
	
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
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
	
	@SubscribeEvent
	public static void register(RegisterEvent e)
	{
		e.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EXCLUSIVE_SKILL_MODIFIERS, Cast.constant(CODEC));
	}
}