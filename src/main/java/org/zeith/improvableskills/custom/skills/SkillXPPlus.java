package org.zeith.improvableskills.custom.skills;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.level.BlockEvent;
import org.zeith.improvableskills.api.evt.CalculateAdditionalFurnaceExperienceMultiplier;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

public class SkillXPPlus
		extends PlayerSkillBase
{
	public SkillXPPlus()
	{
		super(10);
		setupScroll();
		getLoot().chance.n = 3;
		getLoot().setLootTable(EntityType.ELDER_GUARDIAN.getDefaultLootTable());
		setColor(0x93DA34);
		xpCalculator.setBaseFormula("%lvl%^3+(%lvl%+1)*100");
		
		addListener(this::blockBreak);
		addListener(this::killEntity);
		addListener(this::babyEntitySpawn);
		addListener(this::itemFished);
		addListener(this::furnaceExtra);
	}
	
	private void blockBreak(BlockEvent.BreakEvent e)
	{
		PlayerDataManager.handleDataSafely(e.getPlayer(), data ->
		{
			var xp = e.getExpToDrop();
			if(xp <= 0 || !data.isSkillActive(this)) return;
			var xpp = data.getSkillProgress(this);
			e.setExpToDrop(Mth.floor(xp + data.player.level.random.nextFloat() * xp * xpp));
		});
	}
	
	private void killEntity(LivingExperienceDropEvent e)
	{
		var ded = e.getEntity();
		var xp = e.getDroppedExperience();
		
		if(ded instanceof Player /* Prevents XP dupe */ || xp <= 0) return;
		
		PlayerDataManager.handleDataSafely(e.getAttackingPlayer(), data ->
		{
			if(!data.isSkillActive(this)) return;
			float xpp = data.getSkillProgress(this);
			e.setDroppedExperience(Mth.floor(xp + data.player.level.random.nextFloat() * xp * xpp));
		});
	}
	
	private void babyEntitySpawn(BabyEntitySpawnEvent e)
	{
		if(e.getChild() instanceof AbstractVillager) return;
		PlayerDataManager.handleDataSafely(e.getCausedByPlayer(), data ->
		{
			if(!data.isSkillActive(this))
				return;
			int xpp = data.getSkillLevel(this);
			if(xpp > 0)
			{
				int xp = 1 + data.player.level.random.nextInt(xpp + 1);
				var c = e.getParentA();
				if(c.level instanceof ServerLevel mp)
					ExperienceOrb.award(mp, c.position(), xp);
			}
		});
	}
	
	private void itemFished(ItemFishedEvent e)
	{
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			if(!data.isSkillActive(this)) return;
			int xpp = data.getSkillLevel(this);
			NonNullList<ItemStack> drops = e.getDrops();
			if(xpp > 0) for(var i = 0; i < drops.size(); ++i)
			{
				int xp = data.player.level.random.nextInt(xpp + 1);
				if(xp < 1) continue;
				if(data.player.level instanceof ServerLevel mp)
					ExperienceOrb.award(mp, data.player.position(), xp);
			}
		});
	}
	
	private void furnaceExtra(CalculateAdditionalFurnaceExperienceMultiplier e)
	{
		PlayerDataManager.handleDataSafely(e.getEntity(), data ->
		{
			if(!data.isSkillActive(this)) return;
			e.addExtraPercent(data.getSkillProgress(this));
		});
	}
}