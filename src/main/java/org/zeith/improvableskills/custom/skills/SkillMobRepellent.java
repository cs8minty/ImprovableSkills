package org.zeith.improvableskills.custom.skills;

import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.util.AABBUtils;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.client.IClientSkillExtensions;
import org.zeith.improvableskills.api.client.ISlotRenderer;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.util.function.Consumer;

public class SkillMobRepellent
		extends PlayerSkillBase
{
	public SkillMobRepellent()
	{
		super(5);
		setupScroll();
		getLoot().chance.n = 1;
		getLoot().addLootTable(BuiltInLootTables.VILLAGE_ARMORER);
		setColor(0xBBA6F3);
		xpCalculator.xpValue = 4;
		xpCalculator.setBaseFormula("(%lvl%+1)^%xpv%");
	}
	
	@Override
	public void initializeClient(Consumer<IClientSkillExtensions> consumer)
	{
		consumer.accept(new IClientSkillExtensions()
		{
			final ResourceLocation hoveredHands = ImprovableSkills.id("textures/skills/mob_repellent_hovered_hands.png");
			final ResourceLocation hoveredNoHands = ImprovableSkills.id("textures/skills/mob_repellent_hovered_no_hands.png");
			final ResourceLocation normalHands = ImprovableSkills.id("textures/skills/mob_repellent_normal_hands.png");
			final ResourceLocation normalNoHands = ImprovableSkills.id("textures/skills/mob_repellent_normal_no_hands.png");
			
			final UV normalUv = new UV(normalNoHands, 0, 0, 256, 256);
			final UV hovUv = new UV(hoveredNoHands, 0, 0, 256, 256);
			
			final ISlotRenderer renderer = (gfx, x, y, width, height, hoverProgress, partialTicks) ->
			{
				var pose = gfx.pose();
				normalUv.render(pose, x, y, width, height);
				
				if(hoverProgress > 0)
				{
					gfx.setColor(1, 1, 1, hoverProgress);
					hovUv.render(pose, x, y, width, height);
					gfx.setColor(1F, 1F, 1F, 1F);
				}
				
				hoverProgress *= 0.95F;
				
				float mulX = width / 24F, mulY = height / 24F;
				
				pose.pushPose();
				pose.translate(x + 10.55F * mulX, y + 11.5F * mulY, 0);
				pose.mulPose(Axis.ZP.rotationDegrees(hoverProgress * 180));
				pose.translate(-0.5F * mulX, -1.5F * mulY, 0);
				pose.scale(width / 256F, height / 256F, width / 256F);
				FXUtils.bindTexture(normalHands);
				RenderUtils.drawTexturedModalRect(gfx, 0, 11, 107, 117, 10, 22);
				if(hoverProgress > 0)
				{
					gfx.setColor(1, 1, 1, hoverProgress);
					FXUtils.bindTexture(hoveredHands);
					RenderUtils.drawTexturedModalRect(gfx, 0, 11, 107, 117, 10, 22);
					gfx.setColor(1F, 1F, 1F, 1F);
				}
				pose.popPose();
				
				pose.pushPose();
				pose.translate(x + 13.45F * mulX, y + 11.5F * mulY, 0);
				pose.mulPose(Axis.ZP.rotationDegrees(-hoverProgress * 180));
				pose.translate(-0.5F * mulX, -1.5F * mulY, 0);
				pose.scale(width / 256F, height / 256F, width / 256F);
				FXUtils.bindTexture(normalHands);
				RenderUtils.drawTexturedModalRect(gfx, 0, 11, 139, 117, 10, 22);
				if(hoverProgress > 0)
				{
					gfx.setColor(1, 1, 1, hoverProgress);
					FXUtils.bindTexture(hoveredHands);
					RenderUtils.drawTexturedModalRect(gfx, 0, 11, 139, 117, 10, 22);
					gfx.setColor(1F, 1F, 1F, 1F);
				}
				pose.popPose();
				
				return true;
			};
			
			@Override
			public ISlotRenderer slotRenderer()
			{
				return renderer;
			}
		});
	}
	
	@Override
	public void tick(PlayerSkillData data, boolean isActive)
	{
		var sp = data.getSkillProgress(this) * 5F;
		if(isActive && data.getPlayer() instanceof ServerPlayer mp && sp > 0F)
		{
			var lvl = mp.level();
			var c = AABBUtils.getCenter(mp.getBoundingBox());
			for(var m : lvl.getEntitiesOfClass(Monster.class, new AABB(c, c).inflate(sp)))
			{
				var mc = AABBUtils.getCenter(m.getBoundingBox());
				
				var dir = mc.subtract(c).normalize().scale(0.15F);
				
				m.push(dir.x, dir.y, dir.z);
			}
		}
	}
}