package org.zeith.improvableskills.custom.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.zeith.improvableskills.api.evt.VibrationEvent;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillSilentFoot
		extends PlayerSkillBase
{
	public SkillSilentFoot()
	{
		super(10);
		setupScroll();
		getLoot().chance.n = 1;
		getLoot().setLootTables(BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY_ICE_BOX);
		setColor(0x027978);
		xpCalculator.xpValue = 4;
		xpCalculator.setBaseFormula("((%lvl%+1)^%xpv%)/3");
		addListener(this::hook);
	}
	
	private void hook(VibrationEvent e)
	{
		if(e.getContext().sourceEntity() instanceof ServerPlayer mp)
		{
			PlayerDataManager.handleDataSafely(mp, data ->
			{
				if(!data.isSkillActive(this))
					return;
				
				double distance = e.getDistance();
				
				// Decrease the radius from listener's radius all the way down to just one block.
				var radius = Mth.lerp(
						data.getSkillProgress(this),
						e.getListener().getListenerRadius(),
						1
				);
				
				if(radius < distance) e.setCanceled(true);
			});
		}
	}
}