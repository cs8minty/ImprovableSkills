package org.zeith.improvableskills.api.registry;

import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.OwnedTexture;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.client.IClientAbilityExtensions;

import java.util.function.Supplier;

public class PlayerAbilityBase
		implements IHasRegistryName, IRegisterListener
{
	public OwnedTexture<PlayerAbilityBase> tex = new OwnedTexture<>(this);
	
	private ResourceLocation id;
	
	protected Supplier<Integer> color = Suppliers.memoize(() -> getRegistryName().toString().hashCode());
	
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
			id = ImprovableSkills.ABILITIES.getKey(this);
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
		this.color = Cast.constant(color);
	}
	
	public int getColor()
	{
		return color.get();
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
	
	public void initializeClient(java.util.function.Consumer<IClientAbilityExtensions> consumer)
	{
	}
}