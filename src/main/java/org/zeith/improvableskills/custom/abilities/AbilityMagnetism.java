package org.zeith.improvableskills.custom.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.client.gui.abil.GuiMagnetism;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.net.PacketSetMagnetismData;

public class AbilityMagnetism
		extends PlayerAbilityBase
{
	public AbilityMagnetism()
	{
		setColor(0xFF00FF);
	}
	
	@Override
	public void onUnlocked(PlayerSkillData data)
	{
		data.magnetism = true;
		data.magnetismRange = 4;
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		if(data.magnetism && data.magnetismRange > 1F)
		{
			var pos = data.player.getBoundingBox().getCenter();
			for(var ie : data.player.level().getEntitiesOfClass(ItemEntity.class, new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z).inflate(data.magnetismRange)))
			{
				ie.setDeltaMovement(
						ie.getDeltaMovement()
								.scale(0.98F)
								.add(
										data.player.position()
												.subtract(ie.position())
												.normalize()
												.multiply(0.1F, 0.2F, 0.1F)
								)
				);
			}
		}
	}
	
	@Override
	public boolean showDisabledIcon(PlayerSkillData data)
	{
		return !data.magnetism;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		PlayerDataManager.handleDataSafely(player, data ->
		{
			if(mouseButton == 1)
				Network.sendToServer(new PacketSetMagnetismData(data.magnetism = !data.magnetism));
			else if(mouseButton == 0)
				Minecraft.getInstance().pushGuiLayer(new GuiMagnetism(data));
		});
	}
}