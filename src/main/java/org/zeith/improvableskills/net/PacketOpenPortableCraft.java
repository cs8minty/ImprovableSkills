package org.zeith.improvableskills.net;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.client.gui.abil.crafting.CraftingMenuPortable;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.init.AbilitiesIS;

@MainThreaded
public class PacketOpenPortableCraft
		implements IPacket
{
	private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");
	
	@Override
	public void serverExecute(PacketContext net)
	{
		ServerPlayer mp = net.getSender();
		PlayerDataManager.handleDataSafely(mp, dat ->
		{
			if(dat.hasAbility(AbilitiesIS.CRAFTER))
			{
				mp.openMenu(new SimpleMenuProvider((windowId, inventory, player) ->
						new CraftingMenuPortable(windowId, inventory, ContainerLevelAccess.create(player.level, player.blockPosition())), CONTAINER_TITLE));
			}
		});
	}
}