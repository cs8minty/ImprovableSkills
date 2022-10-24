package org.zeith.improvableskills.custom.skills;

import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.mixins.AbstractFurnaceBlockEntityAccessor;

public class SkillAcceleratedFurnace
		extends PlayerSkillBase
{
	public SkillAcceleratedFurnace()
	{
		super(15);
		xpCalculator.xpValue = 2;
	}
	
	public static final DustParticleOptions ALCHEMIST = new DustParticleOptions(new Vector3f(1F, 1F, 0F), 1.0F);
	
	@Override
	public void tick(PlayerSkillData data)
	{
		int lvl = data.getSkillLevel(this);
		boolean acquired = lvl > 0;
		
		if(!acquired || data.player.level.isClientSide)
			return;
		
		Level w = data.player.level;
		
		Level level = data.player.level;
		BlockPos center = data.player.blockPosition();
		
		int rad = 3;
		BlockPos.betweenClosed(center.offset(-rad, -rad, -rad), center.offset(rad, rad, rad))
				.forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity tef && tef instanceof AbstractFurnaceBlockEntityAccessor a)
					{
						int burnTime = a.getLitTime();
						int progress = a.getCookingProgress();
						
						if(burnTime > 0 && level.random.nextInt(maxLvl - lvl + 1) == 0)
						{
							int add = 2 * (int) Math.round(Math.sqrt(lvl));
							a.setCookingProgress(progress + add);
							a.setLitTime((int) Math.max(0, burnTime - add * .8F));
							if(a.getCookingProgress() >= a.getCookingTotalTime())
							{
								Recipe<?> recipe = level.getRecipeManager().getRecipeFor(a.getRecipeType(), tef, level).orElse(null);
								a.callBurn(recipe, a.getItems(), tef.getMaxStackSize());
								a.setCookingProgress(0);
							}
						} else if(a.getCookingProgress() < 1)
						{
							BlockState state = w.getBlockState(pos);
							if(state.getValue(AbstractFurnaceBlock.LIT))
							{
								state = state.setValue(AbstractFurnaceBlock.LIT, false);
								w.setBlock(pos, state, 3);
								w.setBlockEntity(tef);
							}
						}
						
						if(level.random.nextInt(9) == 0 && level instanceof ServerLevel sl)
						{
							Direction face = tef.getBlockState().getValue(AbstractFurnaceBlock.FACING);
							Vec3 vec = Vec3.atLowerCornerOf(pos.relative(face));
							face = face.getOpposite();
							vec = vec.add(.5 + face.getStepX() * .5, .65 + face.getStepY() * .5, .5 + face.getStepZ() * .5);
							sl.sendParticles(ALCHEMIST, vec.x, vec.y, vec.z, 1, 0, 0, 0, 0);
						}
					}
				});
	}
}