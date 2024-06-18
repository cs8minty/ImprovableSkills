package org.zeith.improvableskills.api.treasures.drops;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.treasures.TreasureContext;
import org.zeith.improvableskills.api.treasures.TreasureDropBase;

import java.util.List;
import java.util.Optional;

public class TreasureSandDropLootTableItem
		extends TreasureDropBase
{
	public ResourceKey<LootTable> dropTable = BuiltInLootTables.DESERT_PYRAMID;
	public int minLvl;
	
	public TreasureSandDropLootTableItem()
	{
	}
	
	public TreasureSandDropLootTableItem(ResourceKey<LootTable> table, int minLvl)
	{
		this.dropTable = table;
		this.minLvl = minLvl;
	}
	
	@Override
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
		PlayerSkillData data = ctx.data();
		ServerLevel srv = (ServerLevel) ctx.level();
		
		LootParams lctx = new LootParams.Builder(srv)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(ctx.pos()))
				.withParameter(LootContextParams.THIS_ENTITY, data.player)
				.withLuck(data.player.getLuck())
				.create(LootContextParamSets.CHEST);
		
		ObjectArrayList<ItemStack> gen = Optional.ofNullable(
						srv.registryAccess()
								.registryOrThrow(Registries.LOOT_TABLE)
								.get(dropTable)
				).map(lt -> lt.getRandomItems(lctx))
				.orElse(ObjectArrayList.of());
		
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