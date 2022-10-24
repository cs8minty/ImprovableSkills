package org.zeith.improvableskills.api;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.cfg.ConfigsIS;
import org.zeith.improvableskills.net.PacketSyncSkillData;

import java.math.BigInteger;
import java.util.*;

public class PlayerSkillData
		implements INBTSerializable<CompoundTag>
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills-IO");
	
	public Player player;
	
	private final List<String> skillScrolls = new ArrayList<>();
	private final List<String> abilities = new ArrayList<>();
	public BigInteger storageXp = BigInteger.ZERO;
	public CompoundTag persistedData = new CompoundTag();
	public boolean hasCraftedSkillBook = false;
	public boolean enableXPBank = true;
	private boolean hasCraftedSkillBookPrev = false;
	public Map<String, Short> stats = new HashMap<>();
	
	public float enchantPower = 0;
	
	public boolean magnetism;
	public float magnetismRange;
	
	private boolean isInIO = false;
	
	public Player getPlayer()
	{
		if(this == SyncSkills.CLIENT_DATA)
			return ImprovableSkills.PROXY.getClientPlayer();
		return player;
	}
	
	public PlayerSkillData toCurrent(Player playerReference)
	{
		if(player != playerReference)
			this.player = playerReference;
		return this;
	}
	
	public PlayerSkillData(Player player)
	{
		this.player = player;
	}
	
	public short getSkillLevel(PlayerSkillBase stat)
	{
		return stats.getOrDefault(stat.getRegistryName().toString(), (short) 0);
	}
	
	public ResourceLocation prevDim;
	
	public void handleTick()
	{
		long start = System.currentTimeMillis();
		
		if(player == null || player.isSpectator())
			return;
		
		// stat_scrolls.clear();
		
		Map<ResourceLocation, Long> updates = new HashMap<>();
		
		var skillReg = ImprovableSkills.SKILLS();
		
		for(var value : skillReg.getValues())
		{
			long start0 = System.currentTimeMillis();
			value.tick(this);
			updates.put(value.getRegistryName(), System.currentTimeMillis() - start0);
		}
		
		if(!player.level.isClientSide && !Objects.equals(prevDim, player.level.dimension().location()))
		{
			prevDim = player.level.dimension().location();
			sync();
		}
		
		if(!player.level.isClientSide && hasCraftedSkillBookPrev != hasCraftedSkillBook && !hasCraftedSkillBookPrev)
		{
			player.sendSystemMessage(Component.translatable("chat." + ImprovableSkills.MOD_ID + ".guide"));
			hasCraftedSkillBookPrev = true;
			sync();
		}
		
		hasCraftedSkillBookPrev = hasCraftedSkillBook;
		
		if(!player.level.isClientSide)
		{
			var xpBankShown = ConfigsIS.xpBank;
			if(enableXPBank != xpBankShown)
			{
				enableXPBank = true;
				sync();
			}
		}
		
		if(magnetism && magnetismRange > 1F)
		{
			var pos = player.getBoundingBox().getCenter();
			for(var ie : player.level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z).inflate(magnetismRange)))
			{
				ie.setDeltaMovement(
						ie.getDeltaMovement()
								.scale(0.98F)
								.add(
										player.position()
												.subtract(ie.position())
												.normalize()
												.multiply(0.1F, 0.2F, 0.1F)
								)
				);
			}
		}
		
		long end = System.currentTimeMillis();
		
		if(end - start > 50L)
			ImprovableSkills.LOG.warn("Skill tick took too long! ({} ms, expected <50 ms!). Time map: {}",
					(end - start),
					updates.entrySet().stream().sorted(Comparator.<Map.Entry<ResourceLocation, Long>> comparingLong(Map.Entry::getValue).reversed()).toList()
			);
	}
	
	public void sync()
	{
		if(!isInIO && player instanceof ServerPlayer sp)
			PacketSyncSkillData.sync(sp);
	}
	
	public void setSkillLevel(PlayerSkillBase stat, Number lvl)
	{
		stats.put(stat.getRegistryName().toString(), lvl.shortValue());
		sync();
	}
	
	public boolean hasCraftedSkillsBook()
	{
		return hasCraftedSkillBook;
	}
	
	public static PlayerSkillData deserialize(Player player, CompoundTag nbt)
	{
		PlayerSkillData data = new PlayerSkillData(player);
		data.deserializeNBT(nbt);
		return data;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = new CompoundTag();
		
		persistedData.putString("BankXP", storageXp.toString(36));
		persistedData.putBoolean("SkillBookCrafted", hasCraftedSkillBook);
		persistedData.putBoolean("PrevSkillBookCrafted", hasCraftedSkillBookPrev);
		
		persistedData.putBoolean("Magnetism", magnetism);
		persistedData.putFloat("MagnetismRange", magnetismRange);
		
		nbt.putBoolean("EnableXPBank", enableXPBank);
		
		IForgeRegistry<PlayerSkillBase> reg = ImprovableSkills.SKILLS();
		nbt.put("Persisted", persistedData);
		nbt.putFloat("EnchantPower", enchantPower);
		ListTag list = new ListTag();
		for(String sstat : stats.keySet())
		{
			PlayerSkillBase stat = reg.getValue(new ResourceLocation(sstat));
			
			if(stat == null)
			{
				LOG.warn("[SAVE] Skill '" + sstat + "' wasn't found. Maybe you removed the addon? Skipping unregistered skill.");
				continue;
			}
			
			CompoundTag tag = new CompoundTag();
			tag.putString("Id", stat.getRegistryName().toString());
			tag.putShort("Lvl", getSkillLevel(stat));
			list.add(tag);
		}
		nbt.put("Levels", list);
		
		list = new ListTag();
		for(String scroll : skillScrolls)
			list.add(StringTag.valueOf(scroll));
		nbt.put("Scrolls", list);
		
		list = new ListTag();
		for(String scroll : abilities)
			list.add(StringTag.valueOf(scroll));
		nbt.put("Abilities", list);
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		isInIO = true;
		enableXPBank = nbt.getBoolean("EnableXPBank");
		
		IForgeRegistry<PlayerSkillBase> reg = ImprovableSkills.SKILLS();
		ListTag lvls = nbt.getList("Levels", Tag.TAG_COMPOUND);
		for(int i = 0; i < lvls.size(); ++i)
		{
			CompoundTag tag = lvls.getCompound(i);
			String sstat = tag.getString("Id");
			
			PlayerSkillBase stat = reg.getValue(new ResourceLocation(sstat));
			
			if(stat == null)
			{
				LOG.warn("[LOAD] Skill '" + sstat + "' wasn't found. Maybe you removed the addon? Skipping unregistered skill.");
				continue;
			}
			
			setSkillLevel(stat, tag.getShort("Lvl"));
		}
		
		ListTag list = nbt.getList("Scrolls", Tag.TAG_STRING);
		for(int i = 0; i < list.size(); ++i)
			skillScrolls.add(list.getString(i));
		
		list = nbt.getList("Abilities", Tag.TAG_STRING);
		for(int i = 0; i < list.size(); ++i)
			abilities.add(list.getString(i));
		
		enchantPower = nbt.getFloat("EnchantPower");
		
		persistedData = nbt.getCompound("Persisted");
		if(persistedData.contains("BankXP", Tag.TAG_STRING))
			try
			{
				storageXp = new BigInteger(persistedData.getString("BankXP"), 36);
			} catch(Throwable err)
			{
				storageXp = BigInteger.ZERO;
			}
		
		hasCraftedSkillBook = persistedData.getBoolean("SkillBookCrafted");
		hasCraftedSkillBookPrev = persistedData.getBoolean("PrevSkillBookCrafted");
		
		magnetism = persistedData.getBoolean("Magnetism");
		magnetismRange = persistedData.getFloat("MagnetismRange");
		
		isInIO = false;
	}
	
	public boolean atTickRate(int i)
	{
		return i > 0 && player.tickCount % i == 0;
	}
	
	public boolean hasAbility(PlayerAbilityBase ability)
	{
		return ability != null && abilities.contains(ability.getRegistryName().toString());
	}
	
	public boolean hasSkillScroll(PlayerSkillBase skill)
	{
		return skill != null && skillScrolls.contains(skill.getRegistryName().toString());
	}
	
	public boolean unlockAbility(PlayerAbilityBase ability, boolean sync)
	{
		if(ability != null && player != null && !player.level.isClientSide && !abilities.contains(ability.getRegistryName().toString()))
		{
			abilities.add(ability.getRegistryName().toString());
			ability.onUnlocked(this);
			if(sync) sync();
			return true;
		}
		
		return false;
	}
	
	public boolean unlockSkillScroll(PlayerSkillBase skill, boolean sync)
	{
		if(skill != null && skill.getScrollState().hasScroll() && player != null && !player.level.isClientSide && !skillScrolls.contains(skill.getRegistryName().toString()))
		{
			skillScrolls.add(skill.getRegistryName().toString());
			skill.onUnlocked(this);
			if(sync) sync();
			return true;
		}
		
		return false;
	}
	
	public void lockAbility(PlayerAbilityBase ability, boolean sync)
	{
		if(ability != null && player != null && !player.level.isClientSide && abilities.remove(ability.getRegistryName().toString()) && sync)
			sync();
	}
	
	public void lockSkillScroll(PlayerSkillBase skill, boolean sync)
	{
		if(skill != null && player != null && !player.level.isClientSide && skillScrolls.remove(skill.getRegistryName().toString()) && sync)
			sync();
	}
	
	public int getAbilityCount()
	{
		return abilities.size();
	}
}