package org.zeith.improvableskills;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.improvableskills.api.RecipeParchmentFragment;
import org.zeith.improvableskills.api.loot.RandomBoolean;
import org.zeith.improvableskills.api.registry.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.command.CommandImprovableSkills;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;
import org.zeith.improvableskills.init.*;
import org.zeith.improvableskills.mixins.LootTableAccessor;
import org.zeith.improvableskills.proxy.ISClient;
import org.zeith.improvableskills.proxy.ISServer;

import java.util.function.Supplier;

@Mod(ImprovableSkills.MOD_ID)
public class ImprovableSkills
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills");
	public static final String MOD_ID = "improvableskills";
	public static final String MOD_NAME = "Improvable Skills";
	public static final String NBT_DATA_TAG = "ImprovableSkillsData";
	
	public static final ISServer PROXY = DistExecutor.unsafeRunForDist(() -> ISClient::new, () -> ISServer::new);
	
	@CreativeTab.RegisterTab
	public static final CreativeTab TAB = new CreativeTab(new ResourceLocation(MOD_ID, "root"),
			b -> b.icon(ItemsIS.SKILLS_BOOK::getDefaultInstance)
					.title(Component.translatable("itemGroup." + MOD_ID))
					.withTabsBefore(HLConstants.HL_TAB.id())
	);
	
	private static Supplier<IForgeRegistry<PlayerSkillBase>> SKILLS;
	private static Supplier<IForgeRegistry<PlayerAbilityBase>> ABILITIES;
	private static Supplier<IForgeRegistry<PageletBase>> PAGELETS;
	
	public ImprovableSkills()
	{
		CommonMessages.printMessageOnIllegalRedistribution(ImprovableSkills.class,
				LOG, "ImprovableSkills", "https://www.curseforge.com/minecraft/mc-mods/improvable-skills");
		
		LanguageAdapter.registerMod(MOD_ID);
		
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::newRegistries);
		modBus.addListener(this::setup);
		modBus.addListener(this::loadComplete);
		modBus.addListener(this::fingerprintCheck);
		PROXY.register(modBus);
		
		var mcfBus = MinecraftForge.EVENT_BUS;
		
		mcfBus.addListener(this::registerCommands);
		mcfBus.addListener(this::addLoot);
		
		HammerLib.EVENT_BUS.addListener(this::addRecipes);
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
	private void fingerprintCheck(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "ImprovableSkills", "https://www.curseforge.com/minecraft/mc-mods/improvable-skills");
	}
	
	private void registerCommands(RegisterCommandsEvent e)
	{
		CommandImprovableSkills.register(e.getDispatcher(), e.getBuildContext());
	}
	
	private void setup(FMLCommonSetupEvent e)
	{
		TreasuresIS.register();
	}
	
	private void loadComplete(FMLLoadCompleteEvent e)
	{
		ConfigsIS.reloadCosts();
		if(ConfigsIS.config.hasChanged())
			ConfigsIS.config.save();
	}
	
	private void newRegistries(NewRegistryEvent e)
	{
		SKILLS = e.create(new RegistryBuilder<PlayerSkillBase>()
				.setName(new ResourceLocation(MOD_ID, "skills"))
				.disableSync(), reg -> RegistryMapping.report(PlayerSkillBase.class, reg, false));
		
		ABILITIES = e.create(new RegistryBuilder<PlayerAbilityBase>()
				.setName(new ResourceLocation(MOD_ID, "abilities"))
				.disableSync(), reg -> RegistryMapping.report(PlayerAbilityBase.class, reg, false));
		
		PAGELETS = e.create(new RegistryBuilder<PageletBase>()
				.setName(new ResourceLocation(MOD_ID, "pagelets"))
				.disableSync(), reg -> RegistryMapping.report(PageletBase.class, reg, false));
	}
	
	private void addLoot(LootTableLoadEvent e)
	{
		for(var skill : SKILLS())
		{
			var loot = skill.getLoot();
			if(loot != null)
			{
				loot.apply(e);
			}
		}
		
		if(e.getName().toString().toLowerCase().contains("chests/"))
		{
			RandomBoolean bool = new RandomBoolean();
			bool.n = 5;
			
			LOG.info("Injecting parchment into LootTable '" + e.getName() + "'!");
			
			try
			{
				var table = e.getTable();
				var pools = ((LootTableAccessor) table).getPools();
				
				pools.add(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1F))
						.add(EmptyLootItem.emptyItem().setWeight(4))
						.add(LootItem.lootTableItem(ItemsIS.PARCHMENT_FRAGMENT)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1F)))
								.setWeight(1)
								.setQuality(60)
						)
//						.name("parchment_fragment")
						.build());
			} catch(Throwable err)
			{
				ImprovableSkills.LOG.error("Failed to inject parchment into LootTable '" + e.getName() + "'!!!");
				err.printStackTrace();
			}
		}
	}
	
	private void addRecipes(RegisterRecipesEvent e)
	{
		e.shaped()
				.shape("lbl", "pgp", "lbl")
				.map('l', Tags.Items.LEATHER)
				.map('b', Items.BOOK)
				.map('p', Items.PAPER)
				.map('g', Tags.Items.INGOTS_GOLD)
				.result(ItemsIS.SKILLS_BOOK)
				.register();
		
		e.shapeless()
				.add(ItemsIS.PARCHMENT_FRAGMENT)
				.result(new ItemStack(Items.PAPER, 7))
				.register();
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.ANVIL.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.ANVIL),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ANVIL),
						RecipeHelper.fromTag(Tags.Items.GEMS_EMERALD)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.CRAFTER.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.CRAFTER),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.CRAFTING_TABLE),
						RecipeHelper.fromTag(Tags.Items.INGOTS_IRON)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.ENCHANTING.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.ENCHANTING),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ENCHANTING_TABLE),
						RecipeHelper.fromComponent(Items.BOOKSHELF)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.MAGNETISM.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.MAGNETISM),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.ENDER_EYE),
						RecipeHelper.fromComponent(Items.IRON_INGOT),
						RecipeHelper.fromComponent(Items.CHAIN)
				)
		));
		
		e.add(new RecipeParchmentFragment(AbilitiesIS.AUTO_XP_BANK.getRegistryName(),
				ItemAbilityScroll.of(AbilitiesIS.AUTO_XP_BANK),
				NonNullList.of(
						Ingredient.EMPTY,
						RecipeHelper.fromTag(Tags.Items.ENDER_PEARLS),
						RecipeHelper.fromComponent(Items.EXPERIENCE_BOTTLE),
						RecipeHelper.fromComponent(Items.REDSTONE)
				)
		));
	}
	
	public static IForgeRegistry<PlayerSkillBase> SKILLS()
	{
		return SKILLS.get();
	}
	
	public static IForgeRegistry<PlayerAbilityBase> ABILITIES()
	{
		return ABILITIES.get();
	}
	
	public static IForgeRegistry<PageletBase> PAGELETS()
	{
		return PAGELETS.get();
	}
}