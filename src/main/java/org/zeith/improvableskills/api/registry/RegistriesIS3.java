package org.zeith.improvableskills.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.zeith.improvableskills.ImprovableSkills;

public class RegistriesIS3
{
	public static final ResourceKey<? extends Registry<PageletBase>> PAGELET = ResourceKey.createRegistryKey(ImprovableSkills.id("pagelet"));
	public static final ResourceKey<? extends Registry<PlayerSkillBase>> SKILL = ResourceKey.createRegistryKey(ImprovableSkills.id("skill"));
	public static final ResourceKey<? extends Registry<PlayerAbilityBase>> ABILITY = ResourceKey.createRegistryKey(ImprovableSkills.id("ability"));
}