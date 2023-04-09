package org.zeith.improvableskills.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.util.java.ResettableLazy;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SuggestionProvidersIS3
{
	public static final ResettableLazy<ResourceKeyArgument<PlayerSkillBase>> SKILL_ARGUMENT = ResettableLazy.of(() -> ResourceKeyArgument.key(ImprovableSkills.SKILLS().getRegistryKey()));
	public static final ResettableLazy<ResourceKeyArgument<PlayerAbilityBase>> ABILITY_ARGUMENT = ResettableLazy.of(() -> ResourceKeyArgument.key(ImprovableSkills.ABILITIES().getRegistryKey()));
	
	public static SuggestionProvider<CommandSourceStack> abilitySuggestions()
	{
		return (src, builder) ->
		{
			ImprovableSkills.ABILITIES().getKeys().stream()
					.map(ResourceLocation::toString)
					.filter(s -> s.startsWith(builder.getRemaining()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		};
	}
	
	public static SuggestionProvider<CommandSourceStack> skillSuggestions()
	{
		return (src, builder) ->
		{
			ImprovableSkills.SKILLS().getKeys().stream()
					.map(ResourceLocation::toString)
					.filter(s -> s.startsWith(builder.getRemaining()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		};
	}
}