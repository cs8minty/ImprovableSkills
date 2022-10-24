package org.zeith.improvableskills.client.gui.abil.ench;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.client.gui.base.GuiCustomButton;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.OTEFadeOutButton;
import org.zeith.improvableskills.client.rendering.ote.OTESparkle;

import java.util.Random;

public class GuiEnchPowBook
		extends ScreenWTFMojang<ContainerEnchPowBook>
{
	public static final int DEFAULT_GLINT_COLOR = 0xFF8040CC;
	protected static final ResourceLocation OVERLAY = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/book_slot_overlay.png");
	protected static final ResourceLocation MAIN_GUI = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/enchanter_lvl.png");
	
	public GuiEnchPowBook(ContainerEnchPowBook ctr, Inventory inv, Component label)
	{
		super(ctr, inv, label);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		addRenderableWidget(new GuiCustomButton(0, leftPos + imageWidth / 2 - 16, topPos + imageHeight / 2 - 52 - 12, 60, 20, "--> +", this::actionPerformed));
		addRenderableWidget(new GuiCustomButton(1, leftPos + imageWidth / 2 - 16, topPos + imageHeight / 2 - 52 + 12, 60, 20, Component.translatable("gui.back"), this::actionPerformed));
	}
	
	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		Cast.optionally(children().get(0), GuiCustomButton.class)
				.ifPresent(b -> b.setMessage(Component.literal(hasShiftDown() ? "<-- *" : "--> *")));
		
		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
		this.renderTooltip(pose, mouseX, mouseY);
		
		Slot slot = getSlotUnderMouse();
		if(slot != null && slot.container instanceof SimpleInventory && !slot.hasItem())
		{
			renderTooltip(pose, Component.literal("Slot for ").withStyle(ChatFormatting.GRAY).append(Items.BOOK.getDescription().copy().withStyle(ChatFormatting.BOLD)), mouseX, mouseY);
		}
		
		String ln = I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
		
		font.draw(pose, ln, leftPos + (imageWidth - font.width(ln)) / 2, topPos + 3, 4210752);
	}
	
	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		FXUtils.bindTexture(MAIN_GUI);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		FXUtils.bindTexture(OVERLAY);
		RenderUtils.drawFullTexturedModalRect(pose, leftPos + imageWidth / 2 - 36, topPos + 32, 16, 16);
	}
	
	private void actionPerformed(Button buttonIn)
	{
		if(!(buttonIn instanceof GuiCustomButton button)) return;
		
		new OTEFadeOutButton(button, button.id == 1 ? 2 : 20);
		
		int id = button.id;
		if(id == 0 && hasShiftDown())
			id = 11;
		clickMenuButton(id);
		
		ContainerEnchPowBook thus = menu;
		if(thus != null)
		{
			Slot sl = thus.slots.get(thus.slots.size() - 1);
			
			if(id == 11)
			{
				ItemStack item = thus.inventory.getStackInSlot(0);
				
				PlayerSkillData data = SyncSkills.getData();
				
				if((item.isEmpty() || (item.getItem() == Items.BOOK && item.getCount() < 64)) && data != null && data.enchantPower > 0F)
				{
					Random r = new Random();
					
					int[] rgbs = TexturePixelGetter.getAllColors(Items.BOOK.getDefaultInstance());
					int col = rgbs.length == 0 ? DEFAULT_GLINT_COLOR : rgbs[r.nextInt(rgbs.length)];
					
					String ln = I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
					
					double tx = leftPos + (imageWidth - font.width(ln)) / 2;
					double ty = topPos + 3;
					
					for(int i = 0; i < 2; ++i)
						OnTopEffects.effects.add(new OTESparkle(tx + r.nextFloat() * font.width(ln), ty + r.nextFloat() * font.lineHeight, leftPos + sl.x + r.nextInt(16), topPos + sl.y + r.nextInt(16), 30, col));
				}
			}
			
			if(id == 0)
			{
				ItemStack item = thus.inventory.getStackInSlot(0);
				
				PlayerSkillData data = SyncSkills.getData();
				
				if(!item.isEmpty() && item.getItem() == Items.BOOK && data != null && data.enchantPower < 15F)
				{
					Random r = new Random();
					int col = DEFAULT_GLINT_COLOR;
					
					String ln = I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
					
					double tx = leftPos + (imageWidth - font.width(ln)) / 2;
					double ty = topPos + 3;
					
					for(int i = 0; i < 2; ++i)
						OnTopEffects.effects.add(new OTESparkle(leftPos + sl.x + r.nextInt(16), topPos + sl.y + r.nextInt(16), tx + r.nextFloat() * font.width(ln), ty + r.nextFloat() * font.lineHeight, 30, col));
				}
			}
		}
	}
}