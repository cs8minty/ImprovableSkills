package org.zeith.improvableskills;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammerlib.event.player.PlayerLoadedInEvent;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.improvableskills.api.*;
import org.zeith.improvableskills.api.evt.*;
import org.zeith.improvableskills.custom.items.ItemSkillsBook;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.SkillsIS;
import org.zeith.improvableskills.net.NetSkillCalculator;
import org.zeith.improvableskills.net.PacketSyncSkillData;

import java.util.UUID;

@Mod.EventBusSubscriber
public class SkillEvents
{
	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent e)
	{
		if(e.phase == TickEvent.Phase.START)
			PlayerDataManager.handleDataSafely(e.player, PlayerSkillData::handleTick);
	}
	
	@SubscribeEvent
	public static void damageItem(DamageItemEvent e)
	{
		if(e.getEntity() instanceof Player player)
		{
			PlayerDataManager.handleDataSafely(player, data ->
			{
				var chanceToSaveDurability = Mth.lerp(
						data.getSkillLevel(SkillsIS.DEXTEROUS_ARMS) / (float) SkillsIS.DEXTEROUS_ARMS.maxLvl,
						0,
						60
				);
				var rng = player.getRandom();
				for(int i = 0; i < e.getNewDamage(); ++i)
					if(rng.nextInt(100) + 1 < chanceToSaveDurability)
						e.setNewDamage(e.getNewDamage() - 1);
			});
		}
	}
	
	@SubscribeEvent
	public static void vibration(VibrationEvent e)
	{
		if(e.getContext().sourceEntity() instanceof ServerPlayer mp)
		{
			PlayerDataManager.handleDataSafely(mp, data ->
			{
				double distance = e.getDistance();
				
				// Decrease the radius from listener's radius all the way down to just one block.
				var radius = Mth.lerp(
						data.getSkillLevel(SkillsIS.SILENT_FOOT) / (float) SkillsIS.SILENT_FOOT.maxLvl,
						e.getListener().getListenerRadius(),
						1
				);
				
				if(radius < distance) e.setCanceled(true);
			});
		}
	}
	
	@SubscribeEvent
	public static void playerLoadedIn(PlayerLoadedInEvent e)
	{
		ImprovableSkills.LOG.info("Sending skill data to {} ({})", e.getEntity().getGameProfile().getName(), e.getEntity().getGameProfile().getId());
		
		PlayerDataManager.handleDataSafely(e.getEntity(), PlayerSkillData::sync);
		NetSkillCalculator.pack().build().sendTo(e.getEntity());
	}
	
	@SubscribeEvent
	public static void crafting(PlayerEvent.ItemCraftedEvent e)
	{
		/* Check if we craft skills book */
		if(e.getEntity() instanceof ServerPlayer mp && !e.getCrafting().isEmpty() && e.getCrafting().getItem() instanceof ItemSkillsBook)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(mp);
			if(data == null) return;
			data.hasCraftedSkillBook = true;
			PacketSyncSkillData.sync(mp);
		}
	}
	
	@SubscribeEvent
	public static void breakSpeed(PlayerEvent.BreakSpeed e)
	{
		var p = e.getEntity();
		PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		var pos = e.getPosition().orElse(null);
		if(data == null || data.player == null || pos == null)
			return;
		ItemStack item = p.getMainHandItem();
		var tot = Tuples.mutable(1F);
		ImprovableSkills.SKILLS().getValues()
				.stream()
				.flatMap(s -> Cast.optionally(s, IDigSpeedAffectorSkill.class).stream())
				.forEach(d -> tot.setA(tot.a() + d.getDigMultiplier(item, pos, data)));
		e.setNewSpeed(e.getNewSpeed() * tot.a());
	}
	
	@SubscribeEvent
	public static void fall(LivingFallEvent e)
	{
		if(e.getEntity() instanceof Player p)
		{
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int softLandingStatLevel = data.getSkillLevel(SkillsIS.SOFT_LANDING);
			
			float reduce = Math.min(0.5F, Math.max(0.25F, softLandingStatLevel / (float) SkillsIS.SOFT_LANDING.maxLvl));
			reduce = 1.0F - reduce;
			if(softLandingStatLevel > 0)
			{
				e.setDistance(e.getDistance() * reduce);
				p.fallDistance *= reduce;
				e.setDamageMultiplier(e.getDamageMultiplier() * reduce);
			}
		}
	}
	
	@SubscribeEvent
	public static void jump(LivingEvent.LivingJumpEvent e)
	{
		if(e.getEntity() instanceof Player p)
		{
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int leaper = data.getSkillLevel(SkillsIS.LEAPER);
			
			if(leaper > 0)
				p.setDeltaMovement(p.getDeltaMovement().multiply(1F, 1 + (float) leaper / SkillsIS.LEAPER.maxLvl * 0.75F, 1F));
		}
	}
	
	@SubscribeEvent
	public static void blockBroken(BlockEvent.BreakEvent e)
	{
		var p = e.getPlayer();
		float xp = e.getExpToDrop();
		PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null || xp <= 0)
			return;
		float xpp = data.getSkillLevel(SkillsIS.XP_PLUS) / (float) SkillsIS.XP_PLUS.maxLvl;
		e.setExpToDrop(Mth.floor(xp + p.level.random.nextFloat() * xp * xpp));
	}
	
	@SubscribeEvent
	public static void killEntity(LivingExperienceDropEvent e)
	{
		var die = e.getEntity();
		float xp = e.getDroppedExperience();
		/* Prevent XP dupe */
		if(die instanceof Player || xp <= 0) return;
		
		Player p = e.getAttackingPlayer();
		/** Don't apply anything. */
		if(p == null) return;
		
		PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null) return;
		
		float xpp = data.getSkillLevel(SkillsIS.XP_PLUS) / (float) SkillsIS.XP_PLUS.maxLvl;
		e.setDroppedExperience(Mth.floor(xp + p.level.random.nextFloat() * xp * xpp));
	}
	
	@SubscribeEvent
	public static void babyEntitySpawn(BabyEntitySpawnEvent e)
	{
		Player p = e.getCausedByPlayer();
		if(e.getChild() instanceof Villager || p == null || p instanceof FakePlayer)
			return;
		PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null)
			return;
		int xpp = data.getSkillLevel(SkillsIS.XP_PLUS);
		if(xpp > 0)
		{
			int xp = p.level.random.nextInt(xpp + 1);
			if(xp == 0) return;
			var c = e.getParentA();
			var pos = c.position();
			c.level.addFreshEntity(new ExperienceOrb(c.level, pos.x, pos.y, pos.z, xp));
		}
	}
	
	@SubscribeEvent
	public static void itemFished(ItemFishedEvent e)
	{
		var p = e.getEntity();
		NonNullList<ItemStack> drops = e.getDrops();
		
		if(p == null || p instanceof FakePlayer) return;
		
		PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null) return;
		
		int xpp = data.getSkillLevel(SkillsIS.XP_PLUS);
		if(xpp > 0)
			for(ItemStack drop : drops)
			{
				int x = p.level.random.nextInt(xpp + 1);
				if(x == 0)
					continue;
				var pos = p.position();
				p.level.addFreshEntity(new ExperienceOrb(p.level, pos.x, pos.y, pos.z, x));
			}
	}
	
	@SubscribeEvent
	public static void dropsEvent(HarvestDropsEvent e)
	{
		var p = e.getEntity();
		var pos = e.getPos();
		var w = e.getLevel();
		NonNullList<ItemStack> drops = e.getDrops();
		SkillsIS.TREASURE_OF_SANDS.handleDropAdd(w, pos, PlayerDataManager.getDataFor(p), drops);
	}
	
	@SubscribeEvent
	public static void attackHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		
		ic:
		if(e.getEntity() instanceof Player p && ds == DamageSource.FALL)
		{
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null) break ic;
			if(data.getSkillLevel(SkillsIS.SOFT_LANDING) >= SkillsIS.SOFT_LANDING.maxLvl && e.getAmount() >= p.getHealth())
				e.setAmount(p.getHealth() - 1F);
		}
		
		ic:
		if(ds != null && ds.isFire() && e.getEntity() instanceof Player p)
		{
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null) break ic;
			int obsSkin = data.getSkillLevel(SkillsIS.OBSIDIAN_SKIN);
			e.setAmount(e.getAmount() * (1F - obsSkin / (float) SkillsIS.OBSIDIAN_SKIN.maxLvl + .2F));
		}
		
		ic:
		if(DamageSourceProcessor.getDamageType(ds) == DamageSourceProcessor.DamageType.MELEE)
		{
			Player p = DamageSourceProcessor.getMeleeAttacker(ds);
			if(p == null) break ic;
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			int melee = data.getSkillLevel(SkillsIS.DAMAGE_MELEE);
			float pp = (float) melee / SkillsIS.DAMAGE_MELEE.maxLvl;
			e.setAmount(e.getAmount() + (e.getAmount() * pp / 2F) + pp * 7F);
		}
		
		ic:
		if(DamageSourceProcessor.getDamageType(ds) == DamageSourceProcessor.DamageType.RANGED)
		{
			Player p = DamageSourceProcessor.getRangedOwner(ds);
			if(p == null)
				break ic;
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			int melee = data.getSkillLevel(SkillsIS.DAMAGE_RANGED);
			float pp = (float) melee / SkillsIS.DAMAGE_RANGED.maxLvl;
			e.setAmount(e.getAmount() + (e.getAmount() * pp) + melee / 2F);
		}
		
		ic:
		if(DamageSourceProcessor.getDamageType(ds) == DamageSourceProcessor.DamageType.ALCHEMICAL)
		{
			Player src = DamageSourceProcessor.getAlchemicalOwner(ds.getDirectEntity());
			Entity hurt = e.getEntity();
			if(src == null)
				break ic;
			PlayerSkillData dat = hurt.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(src);
			if(dat == null)
				break ic;
			int lvl = dat.getSkillLevel(SkillsIS.ENDER_MANIPULATOR);
			// ALCHEMICAL DAMAGE !!
		}
		
		if(ds != null && e.getEntity() instanceof Player p)
		{
			PlayerSkillData data = p.level.isClientSide ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int melee = data.getSkillLevel(SkillsIS.PVP);
			float pp = 1 - (float) melee / SkillsIS.PVP.maxLvl;
			e.setAmount(e.getAmount() * Math.min(1, .75F + pp / 4F));
		}
	}
	
	@SubscribeEvent
	public static void enchLvl(EnchantmentLevelSetEvent e)
	{
		var players = e.getLevel().getEntitiesOfClass(ServerPlayer.class, new AABB(e.getPos()).inflate(9));
		for(var p : players)
		{
			if(p.containerMenu instanceof EnchantmentMenu enc)
			{
				/* Check that the item is equal by memory reference. Allows to see who is actually calling the event. Little hack ;) */
				if(e.getItem() == enc.enchantSlots.getItem(0))
				{
					int enchanter = PlayerDataManager.handleDataSafely(p, data -> data.getSkillLevel(SkillsIS.ENCHANTER), 0).intValue();
					if(enchanter > 0 && e.getEnchantLevel() != 0)
						e.setEnchantLevel(Math.max(1, e.getEnchantLevel() - enchanter / 4));
					return;
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void enderPort(EntityTeleportEvent.EnderPearl e)
	{
		var p = e.getPlayer();
		
		if(p != null)
		{
			int lvl = PlayerDataManager.handleDataSafely(p, data -> data.getSkillLevel(SkillsIS.ENDER_MANIPULATOR), 0).intValue();
			
			if(lvl > 0)
			{
				float prog = lvl / (float) (SkillsIS.ENDER_MANIPULATOR.maxLvl - 1);
				
				if(prog > 1)
				{
					e.setAttackDamage(e.getAttackDamage() / 10F);
					p.heal(1);
				} else
					e.setAttackDamage(e.getAttackDamage() * (1F - prog * .8F));
			}
		}
	}
	
	@SubscribeEvent
	public static void respawn(PlayerEvent.PlayerRespawnEvent e)
	{
		if(e.getEntity() instanceof ServerPlayer mp)
			PacketSyncSkillData.sync(mp);
	}
	
	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			MinecraftServer mcs = e.getServer();
			
			PlayerDataManager.DATAS.keySet().removeIf(uuid ->
			{
				var mp = mcs.getPlayerList().getPlayer(UUID.fromString(uuid));
				if(mp == null) return true;
				PlayerSkillData data = PlayerDataManager.DATAS.get(uuid);
				data.player = mp;
				return false;
			});
		}
	}
}