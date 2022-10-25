package org.zeith.improvableskills.custom.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillEnchanter
		extends PlayerSkillBase
{
	public SkillEnchanter()
	{
		super(20);
		setupScroll();
		getLoot().chance.n = 4;
		getLoot().setLootTable(BuiltInLootTables.STRONGHOLD_LIBRARY);
		setColor(0xFF179A);
		xpCalculator.xpValue = 2;
		addListener(this::hook);
	}
	
	private void hook(EnchantmentLevelSetEvent e)
	{
		var players = e.getLevel().getEntitiesOfClass(ServerPlayer.class, new AABB(e.getPos()).inflate(9));
		for(var p : players)
		{
			if(p.containerMenu instanceof EnchantmentMenu ench)
			{
				/* Check that the item is equal by memory reference. Allows to see who is actually calling the event. Little hack ;) */
				if(e.getItem() == ench.enchantSlots.getItem(0))
				{
					int enchanter = PlayerDataManager.handleDataSafely(p, data -> data.isSkillActive(this) ? data.getSkillLevel(this) : 0, 0).intValue();
					if(enchanter > 0 && e.getEnchantLevel() != 0)
						e.setEnchantLevel(Math.max(1, e.getEnchantLevel() - enchanter / 4));
					return;
				}
			}
		}
	}
}