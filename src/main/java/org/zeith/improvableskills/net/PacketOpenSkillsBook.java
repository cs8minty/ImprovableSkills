package org.zeith.improvableskills.net;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.java.Threading;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.SyncSkills;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PageletBase;
import org.zeith.improvableskills.client.gui.base.GuiTabbable;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketOpenSkillsBook
		implements IPacket
{
	public CompoundTag nbt;
	
	public static void sync(ServerPlayer mp)
	{
		if(mp != null)
			PlayerDataManager.handleDataSafely(mp, data -> Network.sendTo(new PacketOpenSkillsBook(data), mp));
	}
	
	PacketOpenSkillsBook(PlayerSkillData data)
	{
		nbt = data.serializeNBT();
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
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getInstance().player, nbt);
		mc.setScreen(GuiTabbable.lastPagelet.createTab(SyncSkills.getData()));
		Threading.createAndStart(() -> ImprovableSkills.PAGELETS().forEach(PageletBase::reload));
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