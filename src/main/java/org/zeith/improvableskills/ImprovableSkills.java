package org.zeith.improvableskills;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.improvableskills.api.recipe.Is3RecipeBuilderExtension;
import org.zeith.improvableskills.api.registry.*;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.command.CommandImprovableSkills;
import org.zeith.improvableskills.init.*;
import org.zeith.improvableskills.proxy.ISClient;
import org.zeith.improvableskills.proxy.ISServer;

@Mod(ImprovableSkills.MOD_ID)
public class ImprovableSkills
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills");
	public static final String MOD_ID = "improvableskills";
	public static final String MOD_NAME = "Improvable Skills";
	
	public static final ISServer PROXY = IProxy.create(() -> ISClient::new, () -> ISServer::new);
	
	@CreativeTab.RegisterTab
	public static final CreativeTab TAB = new CreativeTab(id("root"),
			b -> b.icon(ItemsIS.SKILLS_BOOK::getDefaultInstance)
					.title(Component.translatable("itemGroup." + MOD_ID))
					.withTabsBefore(HLConstants.HL_TAB.id())
	);
	
	public static final Registry<PlayerSkillBase> SKILLS = new RegistryBuilder<>(RegistriesIS3.SKILL).sync(false).create();
	public static final Registry<PlayerAbilityBase> ABILITIES = new RegistryBuilder<>(RegistriesIS3.ABILITY).sync(false).create();
	public static final Registry<PageletBase> PAGELETS = new RegistryBuilder<>(RegistriesIS3.PAGELET).sync(false).create();
	
	public ImprovableSkills(IEventBus modBus)
	{
		CommonMessages.printMessageOnIllegalRedistribution(ImprovableSkills.class,
				LOG, "ImprovableSkills", "https://www.curseforge.com/minecraft/mc-mods/improvable-skills"
		);
		
		LanguageAdapter.registerMod(MOD_ID);
		
		modBus.addListener(this::newRegistries);
		modBus.addListener(this::setup);
		modBus.addListener(this::loadComplete);
		modBus.addListener(this::fingerprintCheck);
		modBus.addListener(this::addRecipes);
		
		PROXY.register(modBus);
		
		var mcfBus = NeoForge.EVENT_BUS;
		
		mcfBus.addListener(this::registerCommands);
	}
	
	public static ResourceLocation id(String path)
	{
		return Resources.location(MOD_ID, path);
	}
	
	private void fingerprintCheck(FMLFingerprintCheckEvent e)
	{
		CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
				LOG, "ImprovableSkills", "https://www.curseforge.com/minecraft/mc-mods/improvable-skills"
		);
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
		RegistryMapping.report(PlayerSkillBase.class, SKILLS, false);
		RegistryMapping.report(PlayerAbilityBase.class, ABILITIES, false);
		RegistryMapping.report(PageletBase.class, PAGELETS, false);
		
		e.register(SKILLS);
		e.register(ABILITIES);
		e.register(PAGELETS);
	}
	
	private void addRecipes(RegisterRecipesEvent e)
	{
		e.shaped()
				.shape("lbl", "pgp", "lbl")
				.map('l', Tags.Items.LEATHERS)
				.map('b', Items.BOOK)
				.map('p', Items.PAPER)
				.map('g', Tags.Items.INGOTS_GOLD)
				.result(ItemsIS.SKILLS_BOOK)
				.register();
		
		e.shapeless()
				.add(ItemsIS.PARCHMENT_FRAGMENT)
				.result(new ItemStack(Items.PAPER, 7))
				.register();
		
		var $ = e.extension(Is3RecipeBuilderExtension.class);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.ANVIL)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.ANVIL,
						Tags.Items.GEMS_EMERALD
				)
				.registerIf(AbilitiesIS.ANVIL::registered);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.CRAFTER)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.CRAFTING_TABLE,
						Tags.Items.INGOTS_IRON
				)
				.registerIf(AbilitiesIS.CRAFTER::registered);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.ENCHANTING)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.ENCHANTING_TABLE,
						Items.BOOKSHELF
				)
				.registerIf(AbilitiesIS.ENCHANTING::registered);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.MAGNETISM)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.ENDER_EYE,
						Items.IRON_INGOT,
						Items.CHAIN
				)
				.registerIf(AbilitiesIS.MAGNETISM::registered);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.AUTO_XP_BANK)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.EXPERIENCE_BOTTLE,
						Items.REDSTONE
				)
				.registerIf(AbilitiesIS.AUTO_XP_BANK::registered);
		
		$.parchment()
				.abilityScroll(AbilitiesIS.COWBOY)
				.addAll(
						Tags.Items.ENDER_PEARLS,
						Items.GLOWSTONE_DUST,
						Items.NETHER_STAR,
						Items.SADDLE
				)
				.registerIf(AbilitiesIS.COWBOY::registered);
	}
}