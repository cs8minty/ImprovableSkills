package org.zeith.improvableskills.net;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.IPacket;
import org.zeith.hammerlib.net.PacketContext;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.AbilitiesIS;
import org.zeith.improvableskills.init.GuiHooksIS;
import org.zeith.improvableskills.utils.GuiManager;

public class PacketOpenPortableAnvil
		implements IPacket
{
	private static final Component CONTAINER_TITLE = Component.translatable("container.repair");
	
	@Override
	public void serverExecute(PacketContext net)
	{
		ServerPlayer mp = net.getSender();
		PlayerDataManager.handleDataSafely(mp, dat ->
		{
			if(AbilitiesIS.ANVIL.registered() && dat.hasAbility(AbilitiesIS.ANVIL))
				GuiManager.openGuiCallback(GuiHooksIS.REPAIR, mp, CONTAINER_TITLE);
		});
	}
}