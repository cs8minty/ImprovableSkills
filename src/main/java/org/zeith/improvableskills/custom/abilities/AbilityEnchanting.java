package org.zeith.improvableskills.custom.abilities;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.Network;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.net.PacketOpenPortableEnch;

public class AbilityEnchanting
		extends PlayerAbilityBase
{
	public AbilityEnchanting()
	{
		setColor(0x007DFF);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		Network.sendToServer(new PacketOpenPortableEnch());
	}
}