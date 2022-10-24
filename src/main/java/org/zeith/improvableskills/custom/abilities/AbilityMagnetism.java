package org.zeith.improvableskills.custom.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.SkillTex;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.client.gui.abil.GuiMagnetism;
import org.zeith.improvableskills.data.PlayerDataManager;

public class AbilityMagnetism
		extends PlayerAbilityBase
{
	public AbilityMagnetism()
	{
		setColor(0xFF00FF);
		
		tex = new SkillTex<>(this)
		{
			@Override
			@OnlyIn(Dist.CLIENT)
			public UV toUV(boolean hovered)
			{
				if(texHov == null || texNorm == null)
				{
					ResourceLocation res = skill.getRegistryName();
					this.texNorm = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_normal.png");
					this.texHov = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_hovered.png");
				}
				
				return new UVWithMagnet(hovered ? texHov : texNorm);
			}
		};
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		PlayerDataManager.handleDataSafely(player, data ->
				Minecraft.getInstance().pushGuiLayer(new GuiMagnetism(data))
		);
	}
	
	@Override
	public void onUnlocked(PlayerSkillData data)
	{
		data.magnetism = true;
		data.magnetismRange = 4;
	}
	
	
	@OnlyIn(Dist.CLIENT)
	static class UVWithMagnet
			extends UV
	{
		public UVWithMagnet(ResourceLocation path)
		{
			super(path, 0, 0, 256, 256);
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public void render(PoseStack pose, float x, float y)
		{
			super.render(pose, x, y);
			
			int size = 96;
			new UV(GuiMagnetism.TEXTURE, 176, SyncSkills.getData().magnetism ? 0 : 20, 20, 20)
					.render(pose, x + 256 - size, y + 256 - size / 2F, size, size);
		}
	}
}