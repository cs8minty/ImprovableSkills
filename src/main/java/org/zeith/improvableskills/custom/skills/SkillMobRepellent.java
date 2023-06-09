package org.zeith.improvableskills.custom.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.zeith.hammerlib.util.AABBUtils;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public class SkillMobRepellent
		extends PlayerSkillBase
{
	public SkillMobRepellent()
	{
		super(5);
		setupScroll();
		getLoot().chance.n = 1;
		getLoot().addLootTable(BuiltInLootTables.VILLAGE_ARMORER);
		setColor(0xBBA6F3);
		xpCalculator.xpValue = 4;
		xpCalculator.setBaseFormula("(%lvl%+1)^%xpv%");
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		var sp = data.getSkillProgress(this) * 5F;
		if(isActive && data.getPlayer() instanceof ServerPlayer mp && sp > 0F)
		{
			var lvl = mp.level();
			var c = AABBUtils.getCenter(mp.getBoundingBox());
			for(var m : lvl.getEntitiesOfClass(Monster.class, new AABB(c, c).inflate(sp)))
			{
				var mc = AABBUtils.getCenter(m.getBoundingBox());
				
				var dir = mc.subtract(c).normalize().scale(0.15F);
				
				m.push(dir.x, dir.y, dir.z);
			}
		}
	}
}