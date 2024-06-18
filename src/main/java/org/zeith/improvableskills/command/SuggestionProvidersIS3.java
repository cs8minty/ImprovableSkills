package org.zeith.improvableskills.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.core.RegistriesHL;
import org.zeith.hammerlib.util.java.ResettableLazy;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.*;

public class SuggestionProvidersIS3
{
	public static final ResourceKeyArgument<PlayerSkillBase> SKILL_ARGUMENT = ResourceKeyArgument.key(RegistriesIS3.SKILL);
	public static final ResourceKeyArgument<PlayerAbilityBase> ABILITY_ARGUMENT = ResourceKeyArgument.key(RegistriesIS3.ABILITY);
	
	public static SuggestionProvider<CommandSourceStack> abilitySuggestions()
	{
		return (src, builder) ->
		{
			ImprovableSkills.ABILITIES
					.keySet()
					.stream()
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
			ImprovableSkills.SKILLS
					.keySet()
					.stream()
					.map(ResourceLocation::toString)
					.filter(s -> s.startsWith(builder.getRemaining()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		};
	}
}