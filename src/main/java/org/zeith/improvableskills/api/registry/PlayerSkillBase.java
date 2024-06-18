package org.zeith.improvableskills.api.registry;

import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.*;
import org.zeith.improvableskills.api.client.IClientSkillExtensions;
import org.zeith.improvableskills.api.loot.SkillLoot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlayerSkillBase
		implements IHasRegistryName, IRegisterListener
{
	private SkillLoot loot;
	private List<Consumer<? extends Event>> forgeEvents = new ArrayList<>();
	
	public SkillCostConfig xpCalculator = new SkillCostConfig(1);
	public OwnedTexture<PlayerSkillBase> tex = new OwnedTexture<>(this);
	protected final int maxLvl;
	protected boolean lockedWithScroll, generateScroll;
	
	protected Supplier<Integer> color = Suppliers.memoize(() -> getRegistryName().toString().hashCode());
	
	public PlayerSkillBase(int maxLvl)
	{
		this.maxLvl = maxLvl;
		initClient();
	}
	
	public float getLevelProgress(int level)
	{
		return level / (float) getMaxLevel();
	}
	
	public int getMaxLevel()
	{
		return maxLvl;
	}
	
	public void setupScroll()
	{
		lockedWithScroll = generateScroll = true;
	}
	
	public void setColor(int color)
	{
		this.color = Cast.constant(color);
	}
	
	public void tick(PlayerSkillData data, boolean isActive)
	{
	}
	
	private ResourceLocation id;
	
	@Override
	public ResourceLocation getRegistryName()
	{
		if(id == null)
			id = ImprovableSkills.SKILLS.getKey(this);
		return id;
	}
	
	@Override
	public String textureFolder()
	{
		return "skills";
	}
	
	public String getUnlocalizedName(ResourceLocation id)
	{
		return "skill." + id.toString();
	}
	
	public String getUnlocalizedName()
	{
		return getUnlocalizedName(getRegistryName());
	}
	
	public String getUnlocalizedName(PlayerSkillData data)
	{
		return getUnlocalizedName();
	}
	
	public MutableComponent getLocalizedName(PlayerSkillData data)
	{
		return Component.translatable(getUnlocalizedName(data) + ".name");
	}
	
	public MutableComponent getLocalizedName()
	{
		return Component.translatable(getUnlocalizedName() + ".name");
	}
	
	public String getUnlocalizedDesc(PlayerSkillData data)
	{
		return "skill." + getRegistryName().toString();
	}
	
	public MutableComponent getLocalizedDesc(PlayerSkillData data)
	{
		return Component.translatable(getUnlocalizedDesc(data) + ".desc");
	}
	
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return xpCalculator.getXPToUpgrade(data, targetLvl);
	}
	
	public boolean canUpgrade(PlayerSkillData data)
	{
		short clvl = data.getSkillLevel(this);
		return clvl < maxLvl && (XPUtil.getXPTotal(data.player) >= getXPToUpgrade(data, (short) (clvl + 1)) || data.player.isCreative());
	}
	
	public void onUpgrade(short oldLvl, short newLvl, PlayerSkillData data)
	{
		if(oldLvl > newLvl)
			XPUtil.setPlayersExpTo(data.player, XPUtil.getXPTotal(data.player) + getXPToDowngrade(data, newLvl));
		else
			XPUtil.setPlayersExpTo(data.player, XPUtil.getXPTotal(data.player) - getXPToUpgrade(data, newLvl));
	}
	
	public boolean isDowngradable(PlayerSkillData data)
	{
		return true;
	}
	
	public int getXPToDowngrade(PlayerSkillData data, short to)
	{
		return getXPToUpgrade(data, (short) to);
	}
	
	public void onDowngrade(PlayerSkillData data, short from)
	{
	}
	
	@Override
	public void onPostRegistered()
	{
		for(var listener : forgeEvents) NeoForge.EVENT_BUS.addListener(listener);
	}
	
	public <T extends Event> void addListener(Consumer<T> consumer)
	{
		forgeEvents.add(consumer);
	}
	
	public EnumScrollState getScrollState()
	{
		return lockedWithScroll ? EnumScrollState.NORMAL : EnumScrollState.NONE;
	}
	
	public SkillLoot getLoot()
	{
		return lockedWithScroll && generateScroll ? (loot == null ? (loot = new SkillLoot(this)) : loot) : null;
	}
	
	public boolean isVisible(PlayerSkillData data)
	{
		return !lockedWithScroll || data.hasSkillScroll(this);
	}
	
	public int getColor()
	{
		return color.get();
	}
	
	public void onUnlocked(PlayerSkillData data)
	{
	}
	
	public boolean is(PlayerSkillBase skill)
	{
		return skill == this;
	}
	
	private Object renderProperties;
	
	public Object getRenderPropertiesInternal()
	{
		return renderProperties;
	}
	
	private void initClient()
	{
		if(FMLEnvironment.dist == Dist.CLIENT && !DatagenModLoader.isRunningDataGen())
		{
			initializeClient(properties ->
			{
				if(properties == this)
					throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
				this.renderProperties = properties;
			});
		}
	}
	
	public void initializeClient(java.util.function.Consumer<IClientSkillExtensions> consumer)
	{
	}
	
	public enum EnumScrollState
	{
		NONE,
		NORMAL,
		SPECIAL;
		
		public boolean hasScroll()
		{
			return ordinal() > 0;
		}
	}
}