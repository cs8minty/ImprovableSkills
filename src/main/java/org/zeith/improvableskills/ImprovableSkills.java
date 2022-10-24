package org.zeith.improvableskills;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.client.adapter.ChatMessageAdapter;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.core.adapter.ModSourceAdapter;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.improvableskills.api.RecipeParchmentFragment;
import org.zeith.improvableskills.api.loot.RandomBoolean;
import org.zeith.improvableskills.api.registry.*;
import org.zeith.improvableskills.command.CommandImprovableSkills;
import org.zeith.improvableskills.custom.items.ItemAbilityScroll;
import org.zeith.improvableskills.init.*;
import org.zeith.improvableskills.proxy.ISClient;
import org.zeith.improvableskills.proxy.ISServer;
import org.zeith.improvableskills.utils.loot.LootEntryItemStack;

import java.util.function.Supplier;

@Mod(ImprovableSkills.MOD_ID)
public class ImprovableSkills
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills");
	public static final String MOD_ID = "improvableskills";
	public static final String MOD_NAME = "Improvable Skills";
	public static final String NBT_DATA_TAG = "ImprovableSkillsData";
	
	public static final ISServer PROXY = DistExecutor.unsafeRunForDist(() -> ISClient::new, () -> ISServer::new);
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(ItemsIS.SKILLS_BOOK);
		}
	};
	
	private static Supplier<IForgeRegistry<PlayerSkillBase>> SKILLS;
	private static Supplier<IForgeRegistry<PlayerAbilityBase>> ABILITIES;
	private static Supplier<IForgeRegistry<PageletBase>> PAGELETS;
	
	public ImprovableSkills()
	{
		var illegalSourceNotice = ModSourceAdapter.getModSource(ImprovableSkills.class)
				.filter(ModSourceAdapter.ModSource::wasDownloadedIllegally)
				.orElse(null);
		
		if(illegalSourceNotice != null)
		{
			LOG.fatal("====================================================");
			LOG.fatal("WARNING: ImprovableSkills was downloaded from " + illegalSourceNotice.referrerDomain() +
					", which has been marked as illegal site over at stopmodreposts.org.");
			LOG.fatal("Please download the mod from https://www.curseforge.com/minecraft/mc-mods/improvable-skills");
			LOG.fatal("====================================================");
			
			var illegalUri = Component.literal(illegalSourceNotice.referrerDomain())
					.withStyle(s -> s.withColor(ChatFormatting.RED));
			var smrUri = Component.literal("stopmodreposts.org")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://stopmodreposts.org/"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			var curseforgeUri = Component.literal("curseforge.com")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/improvable-skills"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			ChatMessageAdapter.sendOnFirstWorldLoad(Component.literal("WARNING: ImprovableSkills was downloaded from ")
					.append(illegalUri)
					.append(", which has been marked as illegal site over at ")
					.append(smrUri)
					.append(". Please download the mod from ")
					.append(curseforgeUri)
					.append(".")
			);
		}
		
		LanguageAdapter.registerMod(MOD_ID);
		
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::newRegistries);
		modBus.addListener(this::setup);
		PROXY.register(modBus);
		
		var mcfBus = MinecraftForge.EVENT_BUS;
		
		mcfBus.addListener(this::registerCommands);
		mcfBus.addListener(this::addLoot);
		
		HammerLib.EVENT_BUS.addListener(this::addRecipes);
	}
	
	private void registerCommands(RegisterCommandsEvent e)
	{
		CommandImprovableSkills.register(e.getDispatcher(), e.getBuildContext());
	}
	
	private void setup(FMLCommonSetupEvent e)
	{
		TreasuresIS.register();
	}
	
	private void newRegistries(NewRegistryEvent e)
	{
		SKILLS = e.create(new RegistryBuilder<PlayerSkillBase>()
				.setName(new ResourceLocation(MOD_ID, "skills"))
				.disableSync(), reg -> RegistryMapping.report(PlayerSkillBase.class, reg));
		
		ABILITIES = e.create(new RegistryBuilder<PlayerAbilityBase>()
				.setName(new ResourceLocation(MOD_ID, "abilities"))
				.disableSync(), reg -> RegistryMapping.report(PlayerAbilityBase.class, reg));
		
		PAGELETS = e.create(new RegistryBuilder<PageletBase>()
				.setName(new ResourceLocation(MOD_ID, "pagelets"))
				.disableSync(), reg -> RegistryMapping.report(PageletBase.class, reg));
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
			
			var entry = LootEntryItemStack.build(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT));
			
			try
			{
				var table = e.getTable();
				table.addPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1F))
						.setBonusRolls(UniformGenerator.between(0F, 1F))
						.add(LootEntryItemStack.simpleBuilder(entry).setWeight(2).setQuality(60))
						.name("parchment_fragment")
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