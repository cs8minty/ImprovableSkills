package org.zeith.improvableskills.custom.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.client.utils.*;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.api.SkillTex;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.net.PacketOpenPortableAnvil;

public class AbilityAnvil
		extends PlayerAbilityBase
{
	public AbilityAnvil()
	{
		setColor(0xFB6400);
		tex = new SkillTex<>(this)
		{
			@Override
			@OnlyIn(Dist.CLIENT)
			public UV toUV(boolean hovered)
			{
				if(texHov == null || texNorm == null)
				{
					ResourceLocation res = owner.getRegistryName();
					this.texNorm = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_normal.png");
					this.texHov = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_hovered.png");
				}
				
				return hovered ? new UVMagma(texHov) : new UV(texNorm, 0, 0, 256, 256);
			}
		};
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		Network.sendToServer(new PacketOpenPortableAnvil());
	}
	
	@OnlyIn(Dist.CLIENT)
	static class UVMagma
			extends UV
	{
		public UVMagma(ResourceLocation path)
		{
			super(path, 0, 0, 256, 256);
		}
		
		final ResourceLocation tex = new ResourceLocation("minecraft:block/lava_flow");
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public void render(PoseStack pose, float x, float y)
		{
			FXUtils.bindTexture(InventoryMenu.BLOCK_ATLAS);
			RenderUtils.drawTexturedModalRect(pose, x + 20, y + 20, RenderUtils.getMainSprite(tex), width - 40, height - 40);
			super.render(pose, x, y);
		}
	}
}