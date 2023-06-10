package org.zeith.improvableskills.api.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.OwnedTexture;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.client.IClientAbilityExtensions;

import java.util.NoSuchElementException;

public class PlayerAbilityBase
		implements IHasRegistryName, IRegisterListener
{
	public OwnedTexture<PlayerAbilityBase> tex = new OwnedTexture<>(this);
	
	private ResourceLocation id;
	
	protected LazyOptional<Integer> color = LazyOptional.of(() -> getRegistryName().toString().hashCode());
	
	public PlayerAbilityBase()
	{
		initClient();
	}
	
	private boolean registered;
	
	@Override
	public void onPostRegistered()
	{
		registered = true;
	}
	
	public boolean registered()
	{
		return registered;
	}
	
	@Override
	public ResourceLocation getRegistryName()
	{
		if(id == null)
			id = ImprovableSkills.ABILITIES().getKey(this);
		return id;
	}
	
	@Override
	public String textureFolder()
	{
		return "abilities";
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
	}
	
	public String getUnlocalizedName()
	{
		return getUnlocalizedName(getRegistryName());
	}
	
	public String getUnlocalizedName(ResourceLocation id)
	{
		return "ability." + id.toString();
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
	
	public void tick(PlayerSkillData data)
	{
	
	}
	
	public boolean showDisabledIcon(PlayerSkillData data)
	{
		return false;
	}
	
	private Object renderProperties;
	
	public Object getRenderPropertiesInternal()
	{
		return renderProperties;
	}
	
	private void initClient()
	{
		if(FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData())
		{
			initializeClient(properties ->
			{
				if(properties == this)
					throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
				this.renderProperties = properties;
			});
		}
	}
	
	public void initializeClient(java.util.function.Consumer<IClientAbilityExtensions> consumer)
	{
	}
}