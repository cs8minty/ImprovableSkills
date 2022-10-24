package org.zeith.improvableskills.api.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;

import java.util.List;
import java.util.function.Supplier;

public abstract class PageletBase
		implements IHasRegistryName
{
	protected Object icon;
	
	public Component title;
	
	public boolean isRight()
	{
		return true;
	}
	
	private ResourceLocation id;
	
	@Override
	public ResourceLocation getRegistryName()
	{
		if(id == null)
			id = ImprovableSkills.PAGELETS().getKey(this);
		return id;
	}
	
	@OnlyIn(Dist.CLIENT)
	public GuiTabbable<?> createTab(PlayerSkillData data)
	{
		return null;
	}
	
	public void reload()
	{
	}
	
	/**
	 * Determines whether this pagelet should perform click even or open another
	 * tab
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean hasTab()
	{
		return true;
	}
	
	/**
	 * Called if {@link #hasTab()} returns false. Otherwise creates new GUI
	 */
	@OnlyIn(Dist.CLIENT)
	public void onClick()
	{
	
	}
	
	public PageletBase setIcon(Object icon)
	{
		this.icon = icon;
		return this;
	}
	
	public PageletBase setTitle(Component title)
	{
		this.title = title;
		return this;
	}
	
	@OnlyIn(Dist.CLIENT)
	public Object getIcon()
	{
		if(this.icon == null)
			return ItemStack.EMPTY;
		if(this.icon instanceof Supplier<?> supp)
			this.icon = supp.get();
		return this.icon;
	}
	
	public void addTitle(List<Component> text)
	{
		if(getTitle() != null)
			text.add(getTitle());
		else
			text.add(Component.literal("Unnamed!"));
	}
	
	public MutableComponent getTitle()
	{
		return title.copy();
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean isVisible(PlayerSkillData data)
	{
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean doesPop()
	{
		return false;
	}
}