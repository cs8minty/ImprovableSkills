package org.zeith.improvableskills.custom.skills;

import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.mixins.BrewingStandBlockEntityAccessor;

public class SkillAlchemist
		extends PlayerSkillBase
{
	public SkillAlchemist()
	{
		super(15);
		xpCalculator.xpValue = 2;
		lockedWithScroll = true;
		generateScroll = true;
		setColor(0xDD783E);
		getLoot().chance.n = 10;
		getLoot().setLootTable(EntityType.WITCH.getDefaultLootTable());
	}
	
	public static final DustParticleOptions ALCHEMIST = new DustParticleOptions(new Vector3f(1F, 1F, 0F), 1.0F);
	
	@Override
	public void tick(PlayerSkillData data)
	{
		int lvl = data.getSkillLevel(this);
		boolean acquired = lvl > 0;
		
		if(!acquired && data.player.level.isClientSide)
			return;
		
		Level level = data.player.level;
		BlockPos center = data.player.blockPosition();
		
		int rad = 3;
		BlockPos.betweenClosed(center.offset(-rad, -rad, -rad), center.offset(rad, rad, rad))
				.forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof BrewingStandBlockEntityAccessor tef)
					{
						int progress = tef.getBrewTime();
						
						if(progress > 0)
						{
							int add = 2 * (int) Math.sqrt(lvl * 2);
							tef.setBrewTime(Math.max(progress - add, 1));
						}
						
						if(level.random.nextInt(9) == 0 && level instanceof ServerLevel sl)
						{
							sl.sendParticles(ALCHEMIST, pos.getX() + 0.5, pos.getY() + 0.85, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
						}
					}
				});
	}
}