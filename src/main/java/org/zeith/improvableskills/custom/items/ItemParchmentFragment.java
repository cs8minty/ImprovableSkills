package org.zeith.improvableskills.custom.items;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.items.ConsumableItem;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.RecipeParchmentFragment;
import org.zeith.improvableskills.init.RecipeTypesIS;

import java.util.List;

public class ItemParchmentFragment
		extends Item
{
	public ItemParchmentFragment(Item.Properties props)
	{
		super(props);
	}
	
	public ItemParchmentFragment()
	{
		this(new Properties());
		ImprovableSkills.TAB.add(this);
	}
	
	@Override
	public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_)
	{
		tooltip.add(HLConstants.CRAFTING_MATERIAL);
		super.appendHoverText(p_41421_, p_41422_, tooltip, p_41424_);
	}
	
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity e)
	{
		if(e == null)
			return false;
		
		float f1 = Mth.sin(((float) e.tickCount) / 10.0F + e.bobOffs) * 0.1F + 0.1F;
		
		var nbt = e.getPersistentData();
		
		boolean fx = false, ffx = false;
		int add = 0;
		RecipeParchmentFragment recipe = null;
		
		var itemsNearby = e.level().getEntitiesOfClass(ItemEntity.class, e.getBoundingBox().inflate(1, .1, 1));
		
		itemsNearby.remove(e); // Exclude self
		
		var recipes = e.level().getRecipeManager();
		
		rs:
		for(var r : recipes.getAllRecipesFor(RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE))
		{
			IntList counts = new IntArrayList();
			NonNullList<ItemStack> copy = NonNullList.create();
			for(var ei : itemsNearby)
			{
				copy.add(ei.getItem().copy());
				counts.add(ei.getItem().getCount());
			}
			SimpleInventory id = new SimpleInventory(copy.size());
			for(int i = 0; i < copy.size(); i++)
				id.items.set(i, copy.get(i));
			
			for(var ci : r.getConsumableIngredients())
				if(!ci.consume(id))
					continue rs;
			
			double minDist = 400;
			
			for(int i = 0; i < copy.size(); ++i)
			{
				boolean changed = copy.get(i).getCount() != counts.getInt(i);
				if(changed)
				{
					var item = itemsNearby.get(i);
					
					double d = Math.max(.8, (1 - item.distanceToSqr(e)) * 15) * 15;
					
					var ep = e.position();
					var ip = item.position();
					
					item.setDeltaMovement(item.getDeltaMovement()
							.add(ep.subtract(ip).scale(1 / d))
					);
					
					item.setNoGravity(true);
					
					minDist = Math.min(item.distanceToSqr(e), minDist);
				}
			}
			
			minDist *= 5000;
			
			fx = true;
			recipe = r;
			
			nbt.putInt("IS3ParchCraft", nbt.getInt("IS3ParchCraft") + 1);
			int v = nbt.getInt("IS3ParchCraft");
			int mv = r.ingredients.size() * 40;
			
			int time = v * 5 / mv;
			
			float prog = v / (float) (mv + 40);
			
			if(v % Math.max(1, 5 - time) == 0)
				e.level().playSound(null, e.blockPosition(), SoundEvents.UI_TOAST_IN, SoundSource.AMBIENT, 2F, .25F + 1.75F * prog);
			
			nbt.putFloat("IS3ParchDegree", nbt.getFloat("IS3ParchDegree") + (prog + .25F) * 4F);
			nbt.putFloat("IS3ParchThrowback", prog);
			
			add = Math.round((v / (mv + 40F)) * 10);
			
			if(v > mv)
			{
				if(v > mv + 40 && minDist < 0.5)
				{
					if(!e.level().isClientSide)
					{
						NonNullList<ItemStack> origin = NonNullList.create();
						for(var ei : itemsNearby)
						{
							origin.add(ei.getItem());
							ei.setNoGravity(false);
						}
						
						id = new SimpleInventory(origin.size());
						for(int i = 0; i < origin.size(); i++)
							id.items.set(i, origin.get(i));
						
						var resStack = r.assemble(id, e.level().registryAccess());
						
						for(ConsumableItem ci : r.getConsumableIngredients())
							if(!ci.consume(id))
								continue rs;
						
						var ep = e.position();
						var res = new ItemEntity(e.level(), ep.x, ep.y, ep.z, resStack);
						
						res.setDeltaMovement(e.getDeltaMovement());
						res.bobOffs = e.bobOffs;
						
						e.level().playSound(null, e.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.AMBIENT, 1F, 1.6F + e.level().random.nextFloat() * .2F);
						
						e.level().addFreshEntity(res);
					}
					
					e.getItem().shrink(1);
					nbt.remove("IS3ParchCraft");
				}
				
				ffx = true;
			}
			
			break rs;
		}
		
		if(recipe == null && nbt.contains("IS3ParchCraft"))
			nbt.remove("IS3ParchCraft");
		
		if(fx && recipe != null && e.tickCount % 2 == 0 && e.onGround())
		{
			int num = recipe.ingredients.size() + 3 + add;
			float deg = 360F / num;
			
			float coff = nbt.getFloat("IS3ParchDegree") % 360F;
			float throwb = 0.75F + nbt.getFloat("IS3ParchThrowback");
			
			for(int i = 0; i < num; ++i)
			{
				double sin = Math.sin(Math.toRadians(coff));
				double cos = Math.cos(Math.toRadians(coff));
				
				var itemRand = e.level().random;
				var ep = e.position();
				
				ImprovableSkills.PROXY.sparkle(e.level(),
						ep.x + (itemRand.nextFloat() - itemRand.nextFloat()) * .05F,
						ep.y + (itemRand.nextFloat() - itemRand.nextFloat()) * .1F + e.getBbHeight() * 1.5,
						ep.z + (itemRand.nextFloat() - itemRand.nextFloat()) * .05F,
						sin * 0.05 * throwb,
						f1 * 0.1,
						cos * 0.05 * throwb,
						0x87B5FF,
						90
				);
				
				coff += deg;
			}
		}
		
		return false;
	}
}