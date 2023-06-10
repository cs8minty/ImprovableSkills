package org.zeith.improvableskills.init;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.treasures.drops.*;

import static org.zeith.improvableskills.api.treasures.TreasureRegistry.registerDrop;

public interface TreasuresIS
{
	static void register()
	{
		registerSandTreasures();
	}
	
	private static void registerSandTreasures()
	{
		registerDrop(new TreasureSandDropItem(1, Stackable.of(new ItemStack(Items.IRON_NUGGET), 1, 3))).setChance(.7F);
		registerDrop(new TreasureSandDropItem(1, new ItemStack(Items.ROTTEN_FLESH))).setChance(.8F);
		registerDrop(new TreasureSandDropItem(1, r -> damage(new ItemStack(select(r, Items.STONE_SHOVEL, Items.STONE_PICKAXE)), 125 - r.nextInt(32)))).setChance(.2F);
		registerDrop(new TreasureSandDropItem(1, Stackable.of(new ItemStack(Items.BONE), 1, 3))).setChance(.65F);
		registerDrop(new TreasureSandDropItem(1, Items.COAL)).setChance(.72F);
		
		registerDrop(new TreasureSandDropItem(2, r -> damage(new ItemStack(select(r, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_SWORD)), 250 - r.nextInt(64)))).setChance(.25F);
		registerDrop(new TreasureSandDropItem(2, Stackable.of(new ItemStack(Items.GOLD_NUGGET), 1, 3))).setChance(.6F);
		registerDrop(new TreasureSandDropItem(2, r -> damage(new ItemStack(r.nextBoolean() ? Items.CHAINMAIL_BOOTS : Items.CHAINMAIL_HELMET), 160 - r.nextInt(69)))).setChance(.1F);
		
		registerDrop(new TreasureSandDropItem(3, new ItemStack(Items.GOLDEN_APPLE, 1))).setChance(.15F);
		registerDrop(new TreasureSandDropItem(3, Stackable.of(new ItemStack(Items.DIAMOND), 1, 2))).setChance(.52F);
		registerDrop(new TreasureSandDropItem(3, Stackable.of(new ItemStack(Items.LAPIS_LAZULI, 1), 3, 7))).setChance(.3F);
		registerDrop(new TreasureSandDropItem(3, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1))).setChance(.001F);
		registerDrop(new TreasureSandDropLootTableItem(BuiltInLootTables.DESERT_PYRAMID, 3)).setChance(.45F);
	}
	
	static ItemStack damage(ItemStack stack, int rng)
	{
		stack.setDamageValue(rng);
		return stack;
	}
	
	static <T> T select(RandomSource rand, T... vars)
	{
		return vars[rand.nextInt(vars.length)];
	}
}