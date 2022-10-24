package org.zeith.improvableskills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.util.shaded.json.JSONObject;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.data.PlayerDataManager;

public class CommandImprovableSkills
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context)
	{
		var $skills = ImprovableSkills.SKILLS();
		var $abilities = ImprovableSkills.ABILITIES();
		
		var skillArg = ResourceKeyArgument.key($skills.getRegistryKey());
		var abilArg = ResourceKeyArgument.key($abilities.getRegistryKey());
		
		SuggestionProvider<CommandSourceStack> skillSuggestions = (src, builder) ->
		{
			$skills.getKeys().stream()
					.map(ResourceLocation::toString)
					.filter(s -> s.startsWith(builder.getRemaining()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		};
		
		SuggestionProvider<CommandSourceStack> abilSuggestions = (src, builder) ->
		{
			$abilities.getKeys().stream()
					.map(ResourceLocation::toString)
					.filter(s -> s.startsWith(builder.getRemaining()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		};
		
		dispatcher.register(
				Commands.literal(ImprovableSkills.MOD_ID)
						.requires(executor -> executor.hasPermission(2))
						
						// Skills sub-command
						.then(Commands.literal("skills")
								.then(Commands.literal("give")
										.then(Commands.argument("targets", EntityArgument.players())
												.then(Commands.literal("only")
														.then(Commands.argument("skill", skillArg)
																.suggests(skillSuggestions)
																.then(Commands.argument("level", IntegerArgumentType.integer(0))
																		.executes((src) ->
																		{
																			var key = src.getArgument("skill", ResourceKey.class);
																			var skill = ImprovableSkills.SKILLS().getValue(key.location());
																			var level = IntegerArgumentType.getInteger(src, "level");
																			
																			if(skill == null)
																				throw new SimpleCommandExceptionType(Component.literal("Skill " + JSONObject.quote(key.location().toString()) + " not found.")).create();
																			
																			if(level > skill.maxLvl)
																				throw new SimpleCommandExceptionType(Component.literal("Unable to set skill level to " + level + ", as the max level for ").append(skill.getLocalizedName()).append(" is " + skill.maxLvl + ".")).create();
																			
																			var updated = 0;
																			
																			for(var player : EntityArgument.getPlayers(src, "targets"))
																			{
																				var pd = PlayerDataManager.getDataFor(player);
																				if(pd != null)
																				{
																					pd.setSkillLevel(skill, level);
																					if(level > 0 && skill.getScrollState().hasScroll())
																						pd.unlockSkillScroll(skill, true);
																					++updated;
																				}
																			}
																			
																			src.getSource().sendSuccess(Component.literal("Skill ").append(skill.getLocalizedName()).append(" set to level " + level + " for " + updated + " players."), true);
																			
																			return updated;
																		})
																)
														)
												)
												.then(Commands.literal("everything")
														.executes((src) ->
														{
															var updated = 0;
															
															for(var player : EntityArgument.getPlayers(src, "targets"))
															{
																var pd = PlayerDataManager.getDataFor(player);
																if(pd != null)
																{
																	for(var skill : ImprovableSkills.SKILLS().getValues())
																	{
																		pd.setSkillLevel(skill, skill.maxLvl);
																		if(skill.getScrollState().hasScroll())
																			pd.unlockSkillScroll(skill, false);
																	}
																	++updated;
																	pd.sync();
																}
															}
															
															src.getSource().sendSuccess(Component.literal("All skills have been given at their max levels for " + updated + " players."), true);
															
															return updated;
														})
												)
										)
								)
								.then(Commands.literal("revoke")
										.then(Commands.argument("targets", EntityArgument.players())
												.then(Commands.literal("only")
														.then(Commands.argument("skill", skillArg)
																.suggests(skillSuggestions)
																.executes((src) ->
																{
																	var key = src.getArgument("skill", ResourceKey.class);
																	var skill = ImprovableSkills.SKILLS().getValue(key.location());
																	var level = 0;
																	
																	if(skill == null)
																		throw new SimpleCommandExceptionType(Component.literal("Skill " + JSONObject.quote(key.location().toString()) + " not found.")).create();
																	
																	var updated = 0;
																	
																	for(var player : EntityArgument.getPlayers(src, "targets"))
																	{
																		var pd = PlayerDataManager.getDataFor(player);
																		if(pd != null)
																		{
																			pd.setSkillLevel(skill, level);
																			pd.lockSkillScroll(skill, true);
																			++updated;
																		}
																	}
																	
																	src.getSource().sendSuccess(Component.literal("Skill ").append(skill.getLocalizedName()).append(" revoked from " + updated + " players."), true);
																	
																	return updated;
																})
														)
												)
												.then(Commands.literal("everything")
														.executes((src) ->
														{
															var updated = 0;
															
															for(var player : EntityArgument.getPlayers(src, "targets"))
															{
																var pd = PlayerDataManager.getDataFor(player);
																if(pd != null)
																{
																	for(var skill : ImprovableSkills.SKILLS().getValues())
																	{
																		pd.setSkillLevel(skill, 0);
																		pd.lockSkillScroll(skill, false);
																	}
																	pd.sync();
																	++updated;
																}
															}
															
															src.getSource().sendSuccess(Component.literal("All skills have been revoked from " + updated + " players."), true);
															
															return updated;
														})
												)
										)
								)
						)
						
						// Abilities sub-command
						.then(Commands.literal("abilities")
								.then(Commands.literal("give")
										.then(Commands.argument("targets", EntityArgument.players())
												.then(Commands.literal("only")
														.then(Commands.argument("ability", abilArg)
																.suggests(abilSuggestions)
																.executes((src) ->
																{
																	var key = src.getArgument("ability", ResourceKey.class);
																	var abil = ImprovableSkills.ABILITIES().getValue(key.location());
																	
																	if(abil == null)
																		throw new SimpleCommandExceptionType(Component.literal("Ability " + JSONObject.quote(key.location().toString()) + " not found.")).create();
																	
																	var updated = 0;
																	
																	for(var player : EntityArgument.getPlayers(src, "targets"))
																	{
																		var pd = PlayerDataManager.getDataFor(player);
																		if(pd != null)
																		{
																			pd.unlockAbility(abil, true);
																			++updated;
																			pd.sync();
																		}
																	}
																	
																	src.getSource().sendSuccess(Component.literal("Ability ").append(abil.getLocalizedName()).append(" unlocked for " + updated + " players."), true);
																	
																	return updated;
																})
														)
												)
												.then(Commands.literal("everything")
														.executes((src) ->
														{
															var updated = 0;
															
															for(var player : EntityArgument.getPlayers(src, "targets"))
															{
																var pd = PlayerDataManager.getDataFor(player);
																if(pd != null)
																{
																	for(var skill : ImprovableSkills.ABILITIES().getValues())
																		pd.unlockAbility(skill, false);
																	++updated;
																	pd.sync();
																}
															}
															
															src.getSource().sendSuccess(Component.literal("All abilities have been unlocked for " + updated + " players."), true);
															
															return updated;
														})
												)
										)
								)
								.then(Commands.literal("revoke")
										.then(Commands.argument("targets", EntityArgument.players())
												.then(Commands.literal("only")
														.then(Commands.argument("ability", abilArg)
																.suggests(abilSuggestions)
																.executes((src) ->
																{
																	var key = src.getArgument("ability", ResourceKey.class);
																	var abil = ImprovableSkills.ABILITIES().getValue(key.location());
																	
																	if(abil == null)
																		throw new SimpleCommandExceptionType(Component.literal("Ability " + JSONObject.quote(key.location().toString()) + " not found.")).create();
																	
																	var updated = 0;
																	
																	for(var player : EntityArgument.getPlayers(src, "targets"))
																	{
																		var pd = PlayerDataManager.getDataFor(player);
																		if(pd != null)
																		{
																			pd.lockAbility(abil, true);
																			++updated;
																			pd.sync();
																		}
																	}
																	
																	src.getSource().sendSuccess(Component.literal("Ability ").append(abil.getLocalizedName()).append(" revoked for " + updated + " players."), true);
																	
																	return updated;
																})
														)
												)
												.then(Commands.literal("everything")
														.executes((src) ->
														{
															var updated = 0;
															
															for(var player : EntityArgument.getPlayers(src, "targets"))
															{
																var pd = PlayerDataManager.getDataFor(player);
																if(pd != null)
																{
																	for(var skill : ImprovableSkills.ABILITIES().getValues())
																		pd.lockAbility(skill, false);
																	++updated;
																	pd.sync();
																}
															}
															
															src.getSource().sendSuccess(Component.literal("All abilities have been revoked for " + updated + " players."), true);
															
															return updated;
														})
												)
										)
								)
						)
		);
	}
}