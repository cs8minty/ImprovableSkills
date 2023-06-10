package org.zeith.improvableskills.api.recipe;

import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.items.ConsumableItem;
import org.zeith.improvableskills.init.RecipeTypesIS;

import java.util.List;

public class RecipeParchmentFragment
		implements Recipe<Container>
{
	public final ResourceLocation id;
	public final List<Ingredient> ingredients;
	public final ItemStack result;
	public final String group;
	
	public RecipeParchmentFragment(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		this.id = id;
		this.group = group;
		this.result = result;
		this.ingredients = ingredients;
	}
	
	public List<ConsumableItem> getConsumableIngredients()
	{
		return ingredients
				.stream()
				.map(i -> new ConsumableItem(1, i))
				.toList();
	}
	
	@Override
	public boolean matches(Container ctr, Level lvl)
	{
		return false;
	}
	
	@Override
	public ItemStack assemble(Container ctr, RegistryAccess access)
	{
		return result.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int x, int y)
	{
		return false;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		return result.copy();
	}
	
	@Override
	public ResourceLocation getId()
	{
		return id;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RecipeTypesIS.PARCHMENT_FRAGMENT_SERIALIZER;
	}
	
	@Override
	public RecipeType<?> getType()
	{
		return RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE;
	}
	
	@Override
	public String getGroup()
	{
		return group;
	}
	
	public static class Serializer
			implements RecipeSerializer<RecipeParchmentFragment>
	{
		@Override
		public RecipeParchmentFragment fromJson(ResourceLocation id, JsonObject json)
		{
			var items = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
			if(items.isEmpty())
			{
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else
			{
				String s = GsonHelper.getAsString(json, "group", "");
				ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
				return new RecipeParchmentFragment(id, s, result, items);
			}
		}
		
		@Nullable
		@Override
		public RecipeParchmentFragment fromNetwork(ResourceLocation id, FriendlyByteBuf buf)
		{
			String s = buf.readUtf();
			int i = buf.readVarInt();
			NonNullList<Ingredient> items = NonNullList.withSize(i, Ingredient.EMPTY);
			for(int j = 0; j < items.size(); ++j) items.set(j, Ingredient.fromNetwork(buf));
			ItemStack result = buf.readItem();
			return new RecipeParchmentFragment(id, s, result, items);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, RecipeParchmentFragment r)
		{
			buf.writeUtf(r.group);
			buf.writeVarInt(r.ingredients.size());
			for(var ingredient : r.ingredients) ingredient.toNetwork(buf);
			buf.writeItem(r.result);
		}
		
		private static NonNullList<Ingredient> itemsFromJson(JsonArray arr)
		{
			NonNullList<Ingredient> lst = NonNullList.create();
			for(int i = 0; i < arr.size(); ++i)
			{
				Ingredient ingredient = Ingredient.fromJson(arr.get(i));
				if(!ingredient.isEmpty())
					lst.add(ingredient);
			}
			return lst;
		}
	}
}