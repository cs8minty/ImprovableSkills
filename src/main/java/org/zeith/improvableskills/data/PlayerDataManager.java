package org.zeith.improvableskills.data;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.PlayerSkillData;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@EventBusSubscriber
public class PlayerDataManager
{
	public static final Map<String, PlayerSkillData> DATAS = new HashMap<>();
	
	private static ThreadLocal<Player> LPLAYER = ThreadLocal.withInitial(() -> null);
	
	public static void handleDataSafely(Player player, Consumer<PlayerSkillData> acceptor)
	{
		PlayerSkillData psd = getDataFor(player);
		if(psd != null)
			acceptor.accept(psd);
	}
	
	public static <T> T handleDataSafely(Player player, Function<PlayerSkillData, T> acceptor, T defaultValue)
	{
		PlayerSkillData psd = getDataFor(player);
		if(psd != null)
			return acceptor.apply(psd);
		return defaultValue;
	}
	
	public static PlayerSkillData getDataFor(Player player)
	{
		if(player == null || player instanceof FakePlayer)
			return null;
		if(player.level.isClientSide)
			return SyncSkills.getData();
		LPLAYER.set(player);
		PlayerSkillData psd = getDataFor(player.getGameProfile());
		
		// Update player reference -- keep it up-to-date
		if(psd != null && psd.getPlayer() != player)
			DATAS.put(player.getGameProfile().getId().toString(), psd = PlayerSkillData.deserialize(player, psd.serializeNBT()));
		
		return psd;
	}
	
	public static PlayerSkillData getDataFor(GameProfile player)
	{
		if(player == null)
			return null;
		return getDataFor(player.getId());
	}
	
	public static PlayerSkillData getDataFor(UUID player)
	{
		if(player == null)
			return null;
		String u = player.toString();
		if(DATAS.containsKey(u))
			return DATAS.get(u);
		Player epl = LPLAYER.get();
		if(epl instanceof ServerPlayer mp)
		{
		}
		return null;
	}
	
	static final List<String> logoff = new ArrayList<String>();
	
	@SubscribeEvent
	public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e)
	{
		logoff.add(e.getEntity().getGameProfile().getId().toString());
	}
	
	@SubscribeEvent
	public static void loadPlayerFromFile(PlayerEvent.LoadFromFile e)
	{
		CompoundTag nbttagcompound = null;
		
		try
		{
			File file1 = e.getPlayerFile(".is3.dat");
			
			if(file1.exists() && file1.isFile())
			{
				nbttagcompound = NbtIo.readCompressed(new FileInputStream(file1));
			}
		} catch(Exception var4)
		{
			ImprovableSkills.LOG.warn("Failed to load player data for {}", e.getEntity().getName());
		}
		
		if(nbttagcompound != null)
			DATAS.put(e.getPlayerUUID(), PlayerSkillData.deserialize(e.getEntity(), nbttagcompound));
		else
			DATAS.put(e.getPlayerUUID(), new PlayerSkillData(e.getEntity()));
	}
	
	@SubscribeEvent
	public static void savePlayerToFile(PlayerEvent.SaveToFile e)
	{
		PlayerSkillData data = getDataFor(e.getEntity());
		if(data == null)
			return;
		try
		{
			CompoundTag nbttagcompound = data.serializeNBT();
			File file1 = e.getPlayerFile(".is3.dat.tmp");
			File file2 = e.getPlayerFile(".is3.dat");
			NbtIo.writeCompressed(nbttagcompound, new FileOutputStream(file1));
			
			if(file2.exists())
			{
				file2.delete();
			}
			
			file1.renameTo(file2);
		} catch(Exception var5)
		{
			ImprovableSkills.LOG.warn("Failed to save player data for {}", e.getEntity().getName());
		}
	}
}