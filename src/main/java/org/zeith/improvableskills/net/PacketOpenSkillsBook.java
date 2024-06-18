package org.zeith.improvableskills.net;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.data.PlayerDataManager;

@Getter
@MainThreaded
public class PacketOpenSkillsBook
		implements IPacket
{
	private CompoundTag nbt;
	
	public static void sync(ServerPlayer mp)
	{
		if(mp != null)
		{
			PlayerDataManager.handleDataSafely(mp, data ->
			{
				Network.sendTo(new PacketOpenSkillsBook(data.serializeNBT(mp.registryAccess())), mp);
			});
			mp.level().gameEvent(mp, GameEvent.EQUIP, mp.position());
		}
	}
	
	PacketOpenSkillsBook(CompoundTag data)
	{
		nbt = data;
	}
	
	public PacketOpenSkillsBook()
	{
	}
	
	@Override
	public void serverExecute(PacketContext net)
	{
		sync(net.getSender());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext net)
	{
		Minecraft mc = Minecraft.getInstance();
		SyncSkills.handle(mc.player, this);
		mc.setScreen(GuiTabbable.lastPagelet.createTab(SyncSkills.getData()));
		Threading.createAndStart(() -> ImprovableSkills.PAGELETS.forEach(PageletBase::reload));
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeNbt(this.nbt);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.nbt = buf.readNbt();
	}
}