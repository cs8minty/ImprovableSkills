package org.zeith.improvableskills.client.rendering;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.zeith.improvableskills.client.rendering.ote.OTEBook;
import org.zeith.improvableskills.client.rendering.ote.OTEItemStack;

public class ItemToBookHandler
{
	public static void toBook(InteractionHand hand, int time)
	{
		Minecraft mc = Minecraft.getInstance();
		
		Vec2 v = getPosOfHandSlot(hand);
		toBook(Minecraft.getInstance().player.getItemInHand(hand), v.x, v.y, time);
	}
	
	public static void toBook(InteractionHand hand, ItemStack stack, int time)
	{
		Minecraft mc = Minecraft.getInstance();
		
		Vec2 v = getPosOfHandSlot(hand);
		toBook(stack, v.x, v.y, time);
	}
	
	public static void toBook(ItemStack stack, double x, double y, int time)
	{
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		
		OTEBook.show(time + 10);
		OnTopEffects.effects.add(new OTEItemStack(x, y, window.getGuiScaledWidth() - 20, window.getGuiScaledHeight() - 12, time, stack));
	}
	
	public static Vec2 getPosOfHandSlot(InteractionHand hand)
	{
		Minecraft mc = Minecraft.getInstance();
		return getPosOfSlot(hand == InteractionHand.OFF_HAND ? -2 : mc.player.getInventory().selected);
	}
	
	public static Vec2 getPosOfSlot(int sl)
	{
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		int w = window.getGuiScaledWidth();
		int h = window.getGuiScaledHeight();
		float slots = 4.5F;
		float slot = 18;
		return new Vec2(w / 2 - slots * slot + sl * slot + (sl == -2 ? 4 : 0), h - 10);
	}
}