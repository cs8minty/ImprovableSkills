package org.zeith.improvableskills.api.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.*;
import org.zeith.improvableskills.api.loot.SkillLoot;
import org.zeith.improvableskills.cfg.ConfigsIS;

import java.util.NoSuchElementException;

public class PlayerSkillBase
		implements IHasRegistryName, IRegisterListener
{
	private SkillLoot loot;
	public SkillCostConfig xpCalculator = new SkillCostConfig(1);
	public SkillTex<PlayerSkillBase> tex = new SkillTex<>(this);
	public final int maxLvl;
	protected boolean lockedWithScroll, generateScroll;
	
	protected LazyOptional<Integer> color = LazyOptional.of(() -> getRegistryName().toString().hashCode());
	
	public PlayerSkillBase(int maxLvl)
	{
		this.maxLvl = maxLvl;
	}
	
	public float getLevelProgress(int level)
	{
		return level / (float) getMaxLvl();
	}
	
	public int getMaxLvl()
	{
		return maxLvl;
	}
	
	public void setupScroll()
	{
		lockedWithScroll = generateScroll = true;
	}
	
	public void setColor(int color)
	{
		this.color = LazyOptional.of(() -> color);
	}
	
	public void tick(PlayerSkillData data)
	{
	}
	
	private ResourceLocation id;
	
	@Override
	public ResourceLocation getRegistryName()
	{
		if(id == null)
			id = ImprovableSkills.SKILLS().getKey(this);
		return id;
	}
	
	public String getUnlocalizedName()
	{
		return "skill." + getRegistryName().toString();
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
		ConfigsIS.reloadCost(this);
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
		return color.orElseThrow(NoSuchElementException::new);
	}
	
	public void onUnlocked(PlayerSkillData data)
	{
	}
	
	public enum EnumScrollState
	{
		NONE, NORMAL, SPECIAL;
		
		public boolean hasScroll()
		{
			return ordinal() > 0;
		}
	}
}