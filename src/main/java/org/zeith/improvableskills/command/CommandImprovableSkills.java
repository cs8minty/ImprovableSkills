package org.zeith.improvableskills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.zeith.hammerlib.util.shaded.json.JSONObject;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.net.NetSkillCalculator;

public class CommandImprovableSkills
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context)
	{
		dispatcher.register(
				Commands.literal(ImprovableSkills.MOD_ID)
						.requires(executor -> executor.hasPermission(2))
						
						.then(reloadConfigs())
						
						.then(Commands.literal("book")
								.then(unlockSkillBook())
								.then(lockSkillBook())
						)
						
						// Skills sub-command
						.then(Commands.literal("skills")
								.then(giveSkill())
								.then(revokeSkill())
								.then(unlockSkill())
								.then(lockSkill())
						)
						
						// Abilities sub-command
						.then(Commands.literal("abilities")
								.then(giveAbility())
								.then(revokeAbility())
						)
		);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> unlockSkillBook()
	{
		return Commands.literal("unlock")
				.then(Commands.argument("targets", EntityArgument.players())
						.executes((src) ->
						{
							var updated = 0;
							
							for(var player : EntityArgument.getPlayers(src, "targets"))
							{
								var pd = PlayerDataManager.getDataFor(player);
								if(pd != null && !pd.hasCraftedSkillsBook())
								{
									pd.hasCraftedSkillBook = true;
									pd.sync();
									++updated;
								}
							}
							
							src.getSource().sendSuccess(Component.literal("Skills Book has been unlocked for " + updated + " players."), true);
							
							return updated;
						})
						.then(Commands.argument("silent", BoolArgumentType.bool())
								.executes((src) ->
								{
									var updated = 0;
									
									for(var player : EntityArgument.getPlayers(src, "targets"))
									{
										var pd = PlayerDataManager.getDataFor(player);
										if(pd != null && !pd.hasCraftedSkillsBook())
										{
											pd.hasCraftedSkillBook = true;
											if(BoolArgumentType.getBool(src, "silent"))
												pd.hasCraftedSkillBookPrev = true;
											pd.sync();
											++updated;
										}
									}
									
									src.getSource().sendSuccess(Component.literal("Skills Book has been unlocked for " + updated + " players."), true);
									
									return updated;
								})
						)
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> lockSkillBook()
	{
		return Commands.literal("lock")
				.then(Commands.argument("targets", EntityArgument.players())
						.executes((src) ->
						{
							var updated = 0;
							
							for(var player : EntityArgument.getPlayers(src, "targets"))
							{
								var pd = PlayerDataManager.getDataFor(player);
								if(pd != null && pd.hasCraftedSkillsBook())
								{
									pd.hasCraftedSkillBook = false;
									pd.sync();
									++updated;
								}
							}
							
							src.getSource().sendSuccess(Component.literal("Skills Book has been unlocked for " + updated + " players."), true);
							
							return updated;
						})
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> reloadConfigs()
	{
		return Commands.literal("reload")
				.executes(src ->
				{
					ConfigsIS.config.load();
					
					ConfigsIS.reloadCustom(ConfigsIS.config);
					ConfigsIS.reloadCosts();
					
					if(ConfigsIS.config.hasChanged())
						ConfigsIS.config.save();
					
					NetSkillCalculator.pack().build().sendToAll();
					
					src.getSource().sendSuccess(Component.literal("Configs have been reloaded."), true);
					
					return 1;
				});
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> unlockSkill()
	{
		return Commands.literal("unlock")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("skill", SuggestionProvidersIS3.SKILL_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.skillSuggestions())
										.executes((src) ->
										{
											var key = src.getArgument("skill", ResourceKey.class);
											var skill = ImprovableSkills.SKILLS().getValue(key.location());
											
											if(skill == null)
												throw new SimpleCommandExceptionType(Component.literal("Skill " + JSONObject.quote(key.location().toString()) + " not found.")).create();
											
											var updated = 0;
											
											for(var player : EntityArgument.getPlayers(src, "targets"))
											{
												var pd = PlayerDataManager.getDataFor(player);
												if(pd != null && pd.unlockSkillScroll(skill, true))
													++updated;
											}
											
											src.getSource().sendSuccess(Component.literal("Skill ").append(skill.getLocalizedName()).append(" unlocked to " + updated + " players."), true);
											
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
											boolean anyUnlocked = false;
											for(var skill : ImprovableSkills.SKILLS().getValues())
												if(pd.unlockSkillScroll(skill, false))
													anyUnlocked = true;
											if(anyUnlocked)
											{
												pd.sync();
												++updated;
											}
										}
									}
									
									src.getSource().sendSuccess(Component.literal("All skills have been unlocked for " + updated + " players."), true);
									
									return updated;
								})
						)
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> lockSkill()
	{
		return Commands.literal("lock")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("skill", SuggestionProvidersIS3.SKILL_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.skillSuggestions())
										.executes((src) ->
										{
											var key = src.getArgument("skill", ResourceKey.class);
											var skill = ImprovableSkills.SKILLS().getValue(key.location());
											
											if(skill == null)
												throw new SimpleCommandExceptionType(Component.literal("Skill " + JSONObject.quote(key.location().toString()) + " not found.")).create();
											
											var updated = 0;
											
											for(var player : EntityArgument.getPlayers(src, "targets"))
											{
												var pd = PlayerDataManager.getDataFor(player);
												if(pd != null && pd.lockSkillScroll(skill, false))
												{
													pd.setSkillLevel(skill, 0);
													++updated;
												}
											}
											
											src.getSource().sendSuccess(Component.literal("Skill ").append(skill.getLocalizedName()).append(" locked to " + updated + " players."), true);
											
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
											boolean anyLocked = false;
											for(var skill : ImprovableSkills.SKILLS().getValues())
												if(pd.lockSkillScroll(skill, false))
												{
													pd.setSkillLevelNoSync(skill, 0);
													anyLocked = true;
												}
											if(anyLocked)
											{
												pd.sync();
												++updated;
											}
										}
									}
									
									src.getSource().sendSuccess(Component.literal("All skills have been locked for " + updated + " players."), true);
									
									return updated;
								})
						)
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> revokeSkill()
	{
		return Commands.literal("revoke")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("skill", SuggestionProvidersIS3.SKILL_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.skillSuggestions())
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
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> giveSkill()
	{
		return Commands.literal("give")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("skill", SuggestionProvidersIS3.SKILL_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.skillSuggestions())
										.then(Commands.argument("level", IntegerArgumentType.integer(0))
												.executes((src) ->
												{
													var key = src.getArgument("skill", ResourceKey.class);
													var skill = ImprovableSkills.SKILLS().getValue(key.location());
													var level = IntegerArgumentType.getInteger(src, "level");
													
													if(skill == null)
														throw new SimpleCommandExceptionType(Component.literal("Skill " + JSONObject.quote(key.location().toString()) + " not found.")).create();
													
													if(level > skill.getMaxLevel())
														throw new SimpleCommandExceptionType(Component.literal("Unable to set skill level to " + level + ", as the max level for ").append(skill.getLocalizedName()).append(" is " + skill.getMaxLevel() + ".")).create();
													
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
												pd.setSkillLevel(skill, skill.getMaxLevel());
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
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> revokeAbility()
	{
		return Commands.literal("revoke")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("ability", SuggestionProvidersIS3.ABILITY_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.abilitySuggestions())
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
				);
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> giveAbility()
	{
		return Commands.literal("give")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("only")
								.then(Commands.argument("ability", SuggestionProvidersIS3.ABILITY_ARGUMENT.get())
										.suggests(SuggestionProvidersIS3.abilitySuggestions())
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
				);
	}
}