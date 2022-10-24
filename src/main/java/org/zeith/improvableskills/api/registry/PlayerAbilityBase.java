package org.zeith.improvableskills.api.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.SkillTex;

import java.util.NoSuchElementException;

public class PlayerAbilityBase
		implements IHasRegistryName
{
	public SkillTex<PlayerAbilityBase> tex = new SkillTex<>(this);
	
	private ResourceLocation id;
	
	protected LazyOptional<Integer> color = LazyOptional.of(() -> getRegistryName().toString().hashCode());
	
	@Override
	public ResourceLocation getRegistryName()
	{
		if(id == null)
			id = ImprovableSkills.ABILITIES().getKey(this);
		return id;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
	}
	
	public String getUnlocalizedName()
	{
		return "ability." + getRegistryName().toString();
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
		return "ability." + getRegistryName().toString();
	}
	
	public MutableComponent getLocalizedDesc(PlayerSkillData data)
	{
		return Component.translatable(getUnlocalizedDesc(data) + ".desc");
	}
	
	
	public void setColor(int color)
	{
		this.color = LazyOptional.of(() -> color);
	}
	
	public int getColor()
	{
		return color.orElseThrow(NoSuchElementException::new);
	}
	
	public void onUnlocked(PlayerSkillData data)
	{
	}
}