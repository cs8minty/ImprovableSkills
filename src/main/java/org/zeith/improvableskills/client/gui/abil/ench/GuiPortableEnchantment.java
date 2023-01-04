package org.zeith.improvableskills.client.gui.abil.ench;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix4f;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.client.rendering.OnTopEffects;
import org.zeith.improvableskills.client.rendering.ote.OTESparkle;

import java.util.List;

import static org.zeith.improvableskills.client.gui.abil.ench.GuiEnchPowBook.DEFAULT_GLINT_COLOR;

public class GuiPortableEnchantment
		extends AbstractContainerScreen<ContainerPortableEnchantment>
{
	private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE1 = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/enchanting_table_book_1.png");
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE2 = new ResourceLocation(ImprovableSkills.MOD_ID, "textures/gui/enchanting_table_book_2.png");
	private final RandomSource random = RandomSource.create();
	private BookModel bookModel;
	public int time;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	private ItemStack last = ItemStack.EMPTY;
	
	public GuiPortableEnchantment(ContainerPortableEnchantment container, Inventory playerInv, Component label)
	{
		super(container, playerInv, label);
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
	}
	
	@Override
	public void containerTick()
	{
		super.containerTick();
		this.tickBook();
		
		for(int section = 0; section < 3; ++section)
			if((this.menu).costs[section] > 0 && random.nextInt(6) == 0)
			{
				String ln = I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
				
				float w = random.nextFloat();
				
				float x1 = leftPos + 60 + (108 - this.font.width(ln)) / 2 + w * this.font.width(ln);
				float y1 = topPos + 3 + this.font.lineHeight;
				
				float x2 = leftPos + 60 + w * 108;
				float y2 = topPos + 14 + 19 * section + random.nextFloat() * 19;
				
				OnTopEffects.effects.add(new OTESparkle(x1, y1, x2, y2, 50 + random.nextInt(30), DEFAULT_GLINT_COLOR));
			}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		
		if(mouseButton == 0)
		{
			mouseX -= leftPos;
			mouseY -= topPos;
			
			String ln = I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
			boolean mouseOverChant = mouseX >= 60 + (108 - font.width(ln)) / 2 && mouseY > 3 && mouseX < 60 + (108 - font.width(ln)) / 2 + font.width(ln) && mouseY < 3 + font.lineHeight;
			if(mouseOverChant)
			{
				minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, .7F + random.nextFloat() * .1F));
				this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 122);
				return true;
			}
			
			mouseX += leftPos;
			mouseY += topPos;
		}
		
		for(int k = 0; k < 3; ++k)
		{
			double d0 = mouseX - (double) (i + 60);
			double d1 = mouseY - (double) (j + 14 + 19 * k);
			if(d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.menu.clickMenuButton(this.minecraft.player, k))
			{
				this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, k);
				
				for(int m = 0; m < 10; ++m)
				{
					float x1 = i + 23 + random.nextFloat() * 22;
					float y1 = j + 23 + random.nextFloat() * 12;
					
					float x2 = i + menu.slots.get(0).x + random.nextFloat() * 16;
					float y2 = j + menu.slots.get(0).y + random.nextFloat() * 16;
					
					OnTopEffects.effects.add(new OTESparkle(x1, y1, x2, y2, 40 - random.nextInt(30), 0xFF0087FF));
				}
				
				return true;
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY)
	{
		mouseX -= leftPos;
		mouseY -= topPos;
		
		var ln = Component.literal(I18n.get("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower)).withStyle(ChatFormatting.UNDERLINE);
		boolean mouseOverChant = mouseX >= 60 + (108 - font.width(ln)) / 2 && mouseY > 3 && mouseX < 60 + (108 - font.width(ln)) / 2 + font.width(ln) && mouseY < 3 + font.lineHeight;
		if(mouseOverChant)
			ln = ln.withStyle(ChatFormatting.BLUE);
		this.font.draw(pose, ln, 60 + (108 - font.width(ln)) / 2, 3, 4210752);
		
		super.renderLabels(pose, mouseX, mouseY);
	}
	
	protected void setBlueColor()
	{
		RenderSystem.setShaderColor(0F, 136 / 255F, 1F, 1F);
	}
	
	@Override
	protected void renderBg(PoseStack pose, float partial, int mouseX, int mouseY)
	{
		Lighting.setupForFlatItems();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
		int guiLeft = leftPos;
		int guiTop = topPos;
		this.blit(pose, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
		int k = (int) this.minecraft.getWindow().getGuiScale();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		
		Matrix4f matrix4f = (new Matrix4f()).translation(-0.34F, 0.23F, 0.0F).perspective(((float) Math.PI / 2F), 1.3333334F, 9.0F, 80.0F);
		
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f);
		pose.pushPose();
		PoseStack.Pose posestack$pose = pose.last();
		posestack$pose.pose().identity();
		posestack$pose.normal().identity();
		pose.translate(0.0D, 3.3F, 1984.0D);
		float f = 5.0F;
		pose.scale(5.0F, 5.0F, 5.0F);
		pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
		pose.mulPose(Axis.XP.rotationDegrees(20.0F));
		float f1 = Mth.lerp(partial, this.oOpen, this.open);
		pose.translate((1.0F - f1) * 0.2F, (1.0F - f1) * 0.1F, (1.0F - f1) * 0.25F);
		float f2 = -(1.0F - f1) * 90.0F - 90.0F;
		pose.mulPose(Axis.YP.rotationDegrees(f2));
		pose.mulPose(Axis.XP.rotationDegrees(180.0F));
		float f3 = Mth.lerp(partial, this.oFlip, this.flip) + 0.25F;
		float f4 = Mth.lerp(partial, this.oFlip, this.flip) + 0.75F;
		f3 = (f3 - (float) Mth.fastFloor(f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float) Mth.fastFloor(f4)) * 1.6F - 0.3F;
		if(f3 < 0.0F)
		{
			f3 = 0.0F;
		}
		
		if(f4 < 0.0F)
		{
			f4 = 0.0F;
		}
		
		if(f3 > 1.0F)
		{
			f3 = 1.0F;
		}
		
		if(f4 > 1.0F)
		{
			f4 = 1.0F;
		}
		
		RenderSystem.enableBlend();
		
		this.bookModel.setupAnim(0.0F, f3, f4, f1);
		MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		
		VertexConsumer vertexconsumer = buf.getBuffer(RenderType.entityCutout(ENCHANTMENT_TABLE_BOOK_TEXTURE1));
		this.bookModel.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 0F, 136 / 255F, 1F, 1F);
		
		vertexconsumer = buf.getBuffer(RenderType.entityCutout(ENCHANTMENT_TABLE_BOOK_TEXTURE2));
		this.bookModel.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		
		buf.endBatch();
		pose.popPose();
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
		RenderSystem.restoreProjectionMatrix();
		Lighting.setupFor3DItems();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantmentNames.getInstance().initSeed(this.menu.getEnchantmentSeed());
		int l = this.menu.getGoldCount();
		
		int leftSectionStart = guiLeft + 60;
		int leftSectionPadded = leftSectionStart + 20;
		for(int section = 0; section < 3; ++section)
		{
			this.setBlitOffset(0);
			
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
			int l1 = (this.menu).costs[section];
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			
			if(l1 == 0)
			{
				this.blit(pose, leftSectionStart, guiTop + 14 + 19 * section, 0, 185, 108, 19);
			} else
			{
				String s = "" + l1;
				int i2 = 86 - this.font.width(s);
				FormattedText formattedtext = EnchantmentNames.getInstance().getRandomName(this.font, i2);
				int j2 = 6839882;
				if(((l < section + 1 || this.minecraft.player.experienceLevel < l1) && !this.minecraft.player.getAbilities().instabuild) || this.menu.enchantClue[section] == -1)
				{ // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
					this.blit(pose, leftSectionStart, guiTop + 14 + 19 * section, 0, 185, 108, 19);
					this.blit(pose, leftSectionStart + 1, guiTop + 15 + 19 * section, 16 * section, 239, 16, 16);
					this.font.drawWordWrap(formattedtext, leftSectionPadded, guiTop + 16 + 19 * section, i2, (j2 & 16711422) >> 1);
					j2 = 4226832;
				} else
				{
					int k2 = mouseX - (guiLeft + 60);
					int l2 = mouseY - (guiTop + 14 + 19 * section);
					if(k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19)
					{
						this.blit(pose, leftSectionStart, guiTop + 14 + 19 * section, 0, 204, 108, 19);
						j2 = 16777088;
					} else
					{
						this.blit(pose, leftSectionStart, guiTop + 14 + 19 * section, 0, 166, 108, 19);
					}
					
					this.blit(pose, leftSectionStart + 1, guiTop + 15 + 19 * section, 16 * section, 223, 16, 16);
					this.font.drawWordWrap(formattedtext, leftSectionPadded, guiTop + 16 + 19 * section, i2, j2);
					j2 = 8453920;
				}
				
				this.font.drawShadow(pose, s, (float) (leftSectionPadded + 86 - this.font.width(s)), (float) (guiTop + 16 + 19 * section + 7), j2);
			}
		}
		
	}
	
	@Override
	public void render(PoseStack p_98767_, int p_98768_, int p_98769_, float p_98770_)
	{
		p_98770_ = this.minecraft.getFrameTime();
		this.renderBackground(p_98767_);
		super.render(p_98767_, p_98768_, p_98769_, p_98770_);
		this.renderTooltip(p_98767_, p_98768_, p_98769_);
		boolean flag = this.minecraft.player.getAbilities().instabuild;
		int i = this.menu.getGoldCount();
		
		for(int j = 0; j < 3; ++j)
		{
			int k = (this.menu).costs[j];
			Enchantment enchantment = Enchantment.byId((this.menu).enchantClue[j]);
			int l = (this.menu).levelClue[j];
			int i1 = j + 1;
			if(this.isHovering(60, 14 + 19 * j, 108, 17, p_98768_, p_98769_) && k > 0)
			{
				List<Component> list = Lists.newArrayList();
				list.add((Component.translatable("container.enchant.clue", enchantment == null ? "" : enchantment.getFullname(l))).withStyle(ChatFormatting.WHITE));
				if(enchantment == null)
				{
					list.add(Component.literal(""));
					list.add(Component.translatable("forge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
				} else if(!flag)
				{
					list.add(CommonComponents.EMPTY);
					if(this.minecraft.player.experienceLevel < k)
					{
						list.add(Component.translatable("container.enchant.level.requirement", (this.menu).costs[j]).withStyle(ChatFormatting.RED));
					} else
					{
						MutableComponent mutablecomponent;
						if(i1 == 1)
						{
							mutablecomponent = Component.translatable("container.enchant.lapis.one");
						} else
						{
							mutablecomponent = Component.translatable("container.enchant.lapis.many", i1);
						}
						
						list.add(mutablecomponent.withStyle(i >= i1 ? ChatFormatting.GRAY : ChatFormatting.RED));
						MutableComponent mutablecomponent1;
						if(i1 == 1)
						{
							mutablecomponent1 = Component.translatable("container.enchant.level.one");
						} else
						{
							mutablecomponent1 = Component.translatable("container.enchant.level.many", i1);
						}
						
						list.add(mutablecomponent1.withStyle(ChatFormatting.GRAY));
					}
				}
				
				this.renderComponentTooltip(p_98767_, list, p_98768_, p_98769_);
				break;
			}
		}
	}
	
	public void tickBook()
	{
		ItemStack itemstack = this.menu.getSlot(0).getItem();
		if(!ItemStack.matches(itemstack, this.last))
		{
			this.last = itemstack;
			
			do
			{
				this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));
			} while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
		}
		
		++this.time;
		this.oFlip = this.flip;
		this.oOpen = this.open;
		boolean flag = false;
		
		for(int i = 0; i < 3; ++i)
		{
			if((this.menu).costs[i] != 0)
			{
				flag = true;
				break;
			}
		}
		
		if(flag)
		{
			this.open += 0.2F;
		} else
		{
			this.open -= 0.2F;
		}
		
		this.open = Mth.clamp(this.open, 0.0F, 1.0F);
		float f1 = (this.flipT - this.flip) * 0.4F;
		float f = 0.2F;
		f1 = Mth.clamp(f1, -0.2F, 0.2F);
		this.flipA += (f1 - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}
}