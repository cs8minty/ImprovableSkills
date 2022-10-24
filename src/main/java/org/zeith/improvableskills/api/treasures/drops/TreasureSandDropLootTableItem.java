package org.zeith.improvableskills.api.treasures.drops;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.treasures.TreasureContext;
import org.zeith.improvableskills.api.treasures.TreasureDropBase;

import java.util.List;

public class TreasureSandDropLootTableItem
		extends TreasureDropBase
{
	public ResourceLocation dropTable = BuiltInLootTables.DESERT_PYRAMID;
	public int minLvl;
	
	public TreasureSandDropLootTableItem()
	{
	}
	
	public TreasureSandDropLootTableItem(ResourceLocation table, int minLvl)
	{
		this.dropTable = table;
		this.minLvl = minLvl;
	}
	
	@Override
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
		PlayerSkillData data = ctx.data();
		ServerLevel srv = (ServerLevel) ctx.level();
		
		LootContext lctx = new LootContext.Builder(srv)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(ctx.pos()))
				.withParameter(LootContextParams.THIS_ENTITY, data.player)
				.withLuck(data.player.getLuck())
				.create(LootContextParamSets.CHEST);
		
		List<ItemStack> gen = srv.getServer()
				.getLootTables()
				.get(dropTable)
				.getRandomItems(lctx);
		
		if(!gen.isEmpty())
		{
			drops.add(gen.get(data.player.getRandom().nextInt(gen.size())).copy());
			return;
		}
	}
	
	@Override
	public TreasureDropBase copy()
	{
		TreasureSandDropLootTableItem l = (TreasureSandDropLootTableItem) super.copy();
		l.dropTable = dropTable;
		l.minLvl = minLvl;
		return l;
	}
	
	@Override
	public boolean canDrop(TreasureContext ctx)
	{
		return ctx.caller() != null && ctx.caller().getRegistryName().toString().equals("improvableskills:treasure_sands") && ctx.data().getSkillLevel(ctx.caller()) >= minLvl;
	}
}