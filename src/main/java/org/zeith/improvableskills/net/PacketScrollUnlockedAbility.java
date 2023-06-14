package org.zeith.improvableskills.net;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.INBTPacket;
import org.zeith.hammerlib.net.PacketContext;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.client.rendering.ItemToBookHandler;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.OTEBook;
import org.zeith.improvableskills.client.rendering.ote.OTEItemAbilityScroll;

import java.util.*;

public class PacketScrollUnlockedAbility
		implements INBTPacket
{
	private ResourceLocation[] skills;
	private ItemStack used;
	private int slot;
	
	public PacketScrollUnlockedAbility(int slot, ItemStack used, ResourceLocation... skills)
	{
		this.skills = skills;
		this.used = used;
		this.slot = slot;
	}
	
	public PacketScrollUnlockedAbility()
	{
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		ListTag tags = new ListTag();
		for(ResourceLocation s : skills)
			tags.add(StringTag.valueOf(s.toString()));
		nbt.put("s", tags);
		nbt.putInt("i", slot);
		nbt.put("u", used.serializeNBT());
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		ListTag tags = nbt.getList("s", Tag.TAG_STRING);
		skills = new ResourceLocation[tags.size()];
		for(int i = 0; i < skills.length; ++i)
			skills[i] = new ResourceLocation(tags.getString(i));
		slot = nbt.getInt("i");
		used = ItemStack.of(nbt.getCompound("u"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext net)
	{
		Player sp = Minecraft.getInstance().player;
		
		List<PlayerAbilityBase> base = new ArrayList<>();
		
		for(ResourceLocation skill : skills)
		{
			PlayerAbilityBase sk = ImprovableSkills.ABILITIES().getValue(skill);
			base.add(sk);
			sp.sendSystemMessage(Component.translatable("chat.improvableskills.ability_unlocked", sk.getLocalizedName(SyncSkills.getData())));
		}
		
		Random rand = new Random();
		Minecraft mc = Minecraft.getInstance();
		Window sr = mc.getWindow();
		Vec2 v = ItemToBookHandler.getPosOfHandSlot(slot == -2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
		OTEBook.show(100 + 10 + 40 + 10 * base.size());
		OnTopEffects.effects.add(new OTEItemAbilityScroll(v.x, v.y, sr.getGuiScaledWidth() - 20 - 48 + rand.nextFloat() * 32, sr.getGuiScaledHeight() - 12 - 24 - rand.nextFloat() * 32, 100, used, base.toArray(new PlayerAbilityBase[0])));
	}
}