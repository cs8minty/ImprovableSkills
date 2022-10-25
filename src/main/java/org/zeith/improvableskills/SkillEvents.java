package org.zeith.improvableskills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.improvableskills.api.IDigSpeedAffectorSkill;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.custom.items.ItemSkillsBook;
import org.zeith.improvableskills.data.PlayerDataManager;

@Mod.EventBusSubscriber
public class SkillEvents
{
	@SubscribeEvent
	public static void breakSpeed(PlayerEvent.BreakSpeed e)
	{
		var pos = e.getPosition().orElse(null);
		if(pos == null) return;
		
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			var p = data.player;
			ItemStack item = p.getMainHandItem();
			var tot = Tuples.mutable(1F);
			
			ImprovableSkills.SKILLS().getValues()
					.stream()
					.flatMap(s -> Cast.optionally(s, IDigSpeedAffectorSkill.class).stream())
					.filter(skill -> data.isSkillActive((PlayerSkillBase) skill))
					.forEach(d -> tot.setA(tot.a() + d.getDigMultiplier(item, pos, data)));
			
			e.setNewSpeed(e.getNewSpeed() * tot.a());
		});
	}
	
	@SubscribeEvent
	public static void crafting(PlayerEvent.ItemCraftedEvent e)
	{
		/* Check if we craft skills book */
		if(e.getEntity() instanceof ServerPlayer mp && !e.getCrafting().isEmpty() && e.getCrafting().getItem() instanceof ItemSkillsBook)
			PlayerDataManager.handleDataSafely(mp, data ->
			{
				data.hasCraftedSkillBook = true;
				data.sync();
			});
	}
}