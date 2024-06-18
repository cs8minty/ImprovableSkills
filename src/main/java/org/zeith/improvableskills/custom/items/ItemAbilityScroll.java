package org.zeith.improvableskills.custom.items;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.zeith.hammerlib.api.items.ITabItem;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.Chars;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.recipe.RecipeParchmentFragment;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.api.tooltip.AbilityTooltip;
import org.zeith.improvableskills.custom.items.data.StoredAbility;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.ItemsIS;
import org.zeith.improvableskills.init.RecipeTypesIS;
import org.zeith.improvableskills.net.PacketScrollUnlockedAbility;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class ItemAbilityScroll
		extends Item
		implements ITabItem
{
	private static final Map<String, PlayerAbilityBase> ABILITY_MAP = new HashMap<>();
	
	private static final Object2IntMap<Item> CUSTOM_COLORS = new Object2IntArrayMap<>();
	
	public static void setCustomColor(Item item, int rgb)
	{
		CUSTOM_COLORS.put(item, rgb);
	}
	
	public static int getCustomColor(Item item)
	{
		return CUSTOM_COLORS.getOrDefault(item, 0x00A800);
	}
	
	public ItemAbilityScroll()
	{
		super(new Properties().stacksTo(1));
		ImprovableSkills.TAB.add(this);
	}
	
	@Override
	public @Nullable String getCreatorModId(ItemStack stack)
	{
		var v = getAbilityFromScroll(stack);
		if(v != null) return ImprovableSkills.ABILITIES.getKey(v).getNamespace();
		return null;
	}
	
	@Nullable
	public static PlayerAbilityBase getAbilityFromScroll(ItemStack stack)
	{
		if(!stack.isEmpty() && stack.has(StoredAbility.TYPE))
		{
			var ss = stack.get(StoredAbility.TYPE);
			return ss != null ? ss.ability() : null;
		}
		return null;
	}
	
	public static ItemStack of(PlayerAbilityBase base)
	{
		ItemStack stack = new ItemStack(ItemsIS.ABILITY_SCROLL);
		stack.set(StoredAbility.TYPE, new StoredAbility(base));
		return stack;
	}
	
	public static void getItems(Set<ItemStack> items)
	{
		ImprovableSkills.ABILITIES
				.stream()
				.sorted(Comparator.comparing(PlayerAbilityBase::getUnlocalizedName))
				.forEach(skill -> items.add(ItemAbilityScroll.of(skill)));
	}
	
	@Override
	public CreativeModeTab getItemCategory()
	{
		return ImprovableSkills.TAB.tab();
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, Set<ItemStack> items)
	{
		if(allowedIn(tab))
			getItems(items);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flagIn)
	{
		PlayerAbilityBase base = getAbilityFromScroll(stack);
		if(base == null)
			return;
		tooltip.add(base.getLocalizedName(SyncSkills.getData()).withStyle(ChatFormatting.GRAY));
		if(flagIn.isAdvanced())
			tooltip.add(Component.literal(" - " + base.getRegistryName()).withStyle(ChatFormatting.DARK_GRAY));
		
		if(!ImprovableSkills.PROXY.hasShiftDown())
		{
			tooltip.add(Component.literal(I18n.get("text.improvableskills:shiftfrecipe")
					.replace('&', Chars.SECTION_SIGN)).withStyle(ChatFormatting.GRAY));
			return;
		}
		
		boolean hasAdded = false;
		
		var level = ImprovableSkills.PROXY.getClientLevel();
		if(level == null) return;
		
		for(var holder : level.getRecipeManager().getAllRecipesFor(RecipeTypesIS.PARCHMENT_FRAGMENT_TYPE))
		{
			var recipe = holder.value();
			var result = recipe.result();
			if(result.getItem() != this) continue;
			var match = getAbilityFromScroll(result);
			if(match != base) continue;
			
			var comp = Component.literal("");
			
			int i = 0;
			var it = Stream.concat(Stream.of(Ingredient.of(ItemsIS.PARCHMENT_FRAGMENT)), recipe.getIngredients().stream())
					.map(m ->
					{
						var st = RecipeHelper.cycleIngredientStack(m, 1000L);
						return ((MutableComponent) st.getDisplayName())
								.withStyle(Style.EMPTY.withColor(getCustomColor(st.getItem())));
					})
					.iterator();
			
			while(it.hasNext())
			{
				if(i > 0) comp.append(", ");
				comp.append(it.next());
				++i;
			}
			
			tooltip.add(Component.translatable("recipe.improvableskills:ability", comp)
					.withStyle(ChatFormatting.GRAY));
			hasAdded = true;
		}
		
		if(!hasAdded)
		{
			String ln = I18n.get("recipe." + base.getRegistryName().getNamespace() + ":ability." +
								 base.getRegistryName().getPath()).replace('&', Chars.SECTION_SIGN);
			int i, j;
			while((i = ln.indexOf('<')) != -1 && (j = ln.indexOf('>', i + 1)) != -1)
			{
				String to = ln.substring(i + 1, j);
				String t;
				
				Item it = ctx.registries().lookupOrThrow(Registries.ITEM)
						.get(ResourceKey.create(Registries.ITEM, Resources.location(to)))
						.map(Holder::value)
						.orElse(null);
				if(it != null)
					t = it.getDefaultInstance().getDisplayName().getString();
				else
					t = Component.translatable("text.improvableskills:unresolved_item")
							.withStyle(ChatFormatting.DARK_RED).getString();
				
				ln = ln.replaceAll("<" + to + ">", t);
			}
			tooltip.add(Component.literal(ln).withStyle(ChatFormatting.GRAY));
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
	{
		var held = playerIn.getItemInHand(handIn);
		
		if(worldIn.isClientSide) return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
		
		return PlayerDataManager.handleDataSafely(playerIn, data ->
		{
			PlayerAbilityBase base = getAbilityFromScroll(held);
			
			if(base != null && !data.hasAbility(base) && data.unlockAbility(base, true))
			{
				ItemStack used = held.copy();
				held.shrink(1);
				
				playerIn.swing(handIn);
				worldIn.playSound(null, playerIn.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 1F);
				
				int slot = handIn == InteractionHand.OFF_HAND ? -2 : playerIn.getInventory().selected;
				
				if(playerIn instanceof ServerPlayer)
					Network.sendTo(new PacketScrollUnlockedAbility(slot, used, base.getRegistryName()), playerIn);
				
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
			}
			
			return new InteractionResultHolder<>(InteractionResult.PASS, held);
		}, new InteractionResultHolder<>(InteractionResult.PASS, held));
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return Optional.ofNullable(getAbilityFromScroll(stack)).map(AbilityTooltip::new);
	}
}