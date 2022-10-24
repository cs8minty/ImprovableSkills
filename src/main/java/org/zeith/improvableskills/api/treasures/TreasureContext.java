package org.zeith.improvableskills.api.treasures;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public record TreasureContext(PlayerSkillBase caller,
							  PlayerSkillData data,
							  Level level, BlockPos pos,
							  RandomSource rand)
{
	public static class Builder
	{
		private PlayerSkillBase base;
		private PlayerSkillData data;
		private Level level;
		private BlockPos pos;
		private RandomSource rng;
		
		public Builder withCaller(PlayerSkillBase base)
		{
			this.base = base;
			return this;
		}
		
		public Builder withData(PlayerSkillData data)
		{
			this.data = data;
			return this;
		}
		
		public Builder withLocation(Level level, BlockPos pos)
		{
			this.level = level;
			this.pos = pos;
			return this;
		}
		
		public Builder withRNG(RandomSource rng)
		{
			this.rng = rng;
			return this;
		}
		
		public TreasureContext build()
		{
			if(data == null)
				throw new IllegalStateException("Context must have data about caller!");
			return new TreasureContext(base, data, level, pos, rng != null ? rng : RandomSource.create());
		}
	}
}