package org.zeith.improvableskills.api.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.abstractions.recipes.IRecipeVisualizer;
import org.zeith.hammerlib.abstractions.recipes.IVisualizedRecipe;
import org.zeith.hammerlib.abstractions.recipes.layout.ISlotBuilder.SlotRole;
import org.zeith.hammerlib.abstractions.recipes.layout.IVisualizerBuilder;
import org.zeith.hammerlib.api.items.ConsumableItem;
import org.zeith.hammerlib.api.recipes.BaseCustomRecipe;
import org.zeith.hammerlib.api.registrars.SerializableRecipeType;
import org.zeith.hammerlib.client.render.IGuiDrawable;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.init.RecipeTypesIS;

import java.util.List;
import java.util.function.Consumer;

public class RecipeParchmentFragment
		extends BaseCustomRecipe<RecipeParchmentFragment>
{
	public RecipeParchmentFragment(String group, ItemStack result, List<Ingredient> ingredients)
	{
		super(group);
		this.vanillaResult = result;
		this.vanillaIngredients.addAll(ingredients);
	}
	
	public ItemStack result()
	{
		return vanillaResult.copy();
	}
	
	public List<ConsumableItem> getConsumableIngredients()
	{
		return vanillaIngredients
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
		public static final MapCodec<RecipeParchmentFragment> CODEC = RecordCodecBuilder.mapCodec(inst ->
				inst.group(
						Codec.STRING.lenientOptionalFieldOf("group", "").forGetter(RecipeParchmentFragment::getGroup),
						ItemStack.CODEC.fieldOf("result").forGetter(RecipeParchmentFragment::result),
						Ingredient.LIST_CODEC.fieldOf("ingredients").forGetter(RecipeParchmentFragment::getIngredients)
				).apply(inst, RecipeParchmentFragment::new)
		);
		
		@Override
		public MapCodec<RecipeParchmentFragment> codec()
		{
			return CODEC;
		}
		
		@Nullable
		@Override
		public RecipeParchmentFragment fromNetwork(RegistryFriendlyByteBuf buf)
		{
			String s = buf.readUtf();
			int i = buf.readVarInt();
			NonNullList<Ingredient> items = NonNullList.withSize(i, Ingredient.EMPTY);
			for(int j = 0; j < items.size(); ++j) items.set(j, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
			ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
			return new RecipeParchmentFragment(s, result, items);
		}
		
		@Override
		public void toNetwork(RegistryFriendlyByteBuf buf, RecipeParchmentFragment r)
		{
			buf.writeUtf(r.group);
			buf.writeVarInt(r.getIngredients().size());
			for(var ingredient : r.getIngredients()) Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
			ItemStack.STREAM_CODEC.encode(buf, r.result());
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
					VisualizedTestMachine::new
			));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public record VisualizedTestMachine(RecipeHolder<RecipeParchmentFragment> recipe)
			implements IVisualizedRecipe<RecipeParchmentFragment>
	{
		public static final UV BACKGROUND = new UV(ImprovableSkills.id("textures/gui/jei.png"), 0, 0, 132, 34);
		
		@Override
		public RecipeHolder<RecipeParchmentFragment> getRecipe()
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
					.addItemStack(recipe.value().result())
					.build();
			
			int j = 0;
			for(var ci : recipe.value().getIngredients())
			{
				builder.addSlot(SlotRole.INPUT, j * 18 + 26, 9)
						.addIngredients(ci);
				++j;
			}
		}
	}
}