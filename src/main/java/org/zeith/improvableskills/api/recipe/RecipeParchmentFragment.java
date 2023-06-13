package org.zeith.improvableskills.api.recipe;

import com.google.gson.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.abstractions.recipes.IRecipeVisualizer;
import org.zeith.hammerlib.abstractions.recipes.IVisualizedRecipe;
import org.zeith.hammerlib.abstractions.recipes.layout.ISlotBuilder.SlotRole;
import org.zeith.hammerlib.abstractions.recipes.layout.IVisualizerBuilder;
import org.zeith.hammerlib.api.items.ConsumableItem;
import org.zeith.hammerlib.api.recipes.BaseRecipe;
import org.zeith.hammerlib.api.recipes.SerializableRecipeType;
import org.zeith.hammerlib.client.render.IGuiDrawable;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.init.RecipeTypesIS;

import java.util.List;
import java.util.function.Consumer;

public class RecipeParchmentFragment
		extends BaseRecipe<RecipeParchmentFragment>
{
	public final List<Ingredient> ingredients;
	
	public RecipeParchmentFragment(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		super(id, group);
		this.vanillaResult = result;
		this.ingredients = ingredients;
	}
	
	public ItemStack result()
	{
		return vanillaResult.copy();
	}
	
	public List<ConsumableItem> getConsumableIngredients()
	{
		return ingredients
				.stream()
				.map(i -> new ConsumableItem(1, i))
				.toList();
	}
	
	@Override
	protected SerializableRecipeType<RecipeParchmentFragment> getRecipeType()
	{
		return RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE;
	}
	
	public static class Type
			extends SerializableRecipeType<RecipeParchmentFragment>
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
			buf.writeItem(r.vanillaResult);
		}
		
		@Override
		public void initVisuals(Consumer<IRecipeVisualizer<RecipeParchmentFragment, ?>> viualizerConsumer)
		{
			viualizerConsumer.accept(IRecipeVisualizer.simple(VisualizedTestMachine.class,
					IRecipeVisualizer.groupBuilder()
							.title(Component.translatable("jei." + ImprovableSkills.MOD_ID + ":parchf"))
							.size(132, 34)
							.icon(IGuiDrawable.ofItem(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT)))
							.catalyst(new ItemStack(ItemsIS.SKILLS_BOOK))
							.build(),
					VisualizedTestMachine::new));
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
	
	@OnlyIn(Dist.CLIENT)
	public static class VisualizedTestMachine
			implements IVisualizedRecipe<RecipeParchmentFragment>
	{
		public static final UV BACKGROUND = new UV(ImprovableSkills.id("textures/gui/jei.png"), 0, 0, 132, 34);
		
		final RecipeParchmentFragment recipe;
		
		public VisualizedTestMachine(RecipeParchmentFragment recipe)
		{
			this.recipe = recipe;
		}
		
		@Override
		public RecipeParchmentFragment getRecipe()
		{
			return recipe;
		}
		
		@Override
		public void drawBackground(GuiGraphics gfx, double mouseX, double mouseY)
		{
			BACKGROUND.render(gfx, 0, 0);
		}
		
		@Override
		public void setupLayout(IVisualizerBuilder builder)
		{
			builder.addSlot(SlotRole.INPUT, 8, 9)
					.addItemStack(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT))
					.build();
			
			builder.addSlot(SlotRole.OUTPUT, 107, 9)
					.addItemStack(recipe.vanillaResult.copy())
					.build();
			
			int j = 0;
			for(var ci : recipe.ingredients)
			{
				builder.addSlot(SlotRole.INPUT, j * 18 + 26, 9)
						.addIngredients(ci);
				++j;
			}
		}
	}
}