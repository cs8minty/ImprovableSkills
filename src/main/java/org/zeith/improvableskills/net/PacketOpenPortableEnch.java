package org.zeith.improvableskills.net;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.AbilitiesIS;
import org.zeith.improvableskills.init.GuiHooksIS;
import org.zeith.improvableskills.utils.GuiManager;

@MainThreaded
public class PacketOpenPortableEnch
		implements IPacket
{
	public PacketOpenPortableEnch()
	{
	}
	
	@Override
	public void serverExecute(PacketContext net)
	{
		ServerPlayer mp = net.getSender();
		PlayerDataManager.handleDataSafely(mp, dat ->
		{
			if(AbilitiesIS.ENCHANTING.registered() && dat.hasAbility(AbilitiesIS.ENCHANTING))
				GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, mp, Component.translatable("container.enchant"));
		});
	}
}