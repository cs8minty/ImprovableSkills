package org.zeith.improvableskills.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketSetSkillActivity
		implements IPacket
{
	private ResourceLocation skillId;
	private boolean enabled;
	
	public PacketSetSkillActivity(ResourceLocation skillId, boolean enabled)
	{
		this.skillId = skillId;
		this.enabled = enabled;
	}
	
	public PacketSetSkillActivity()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(skillId);
		buf.writeBoolean(enabled);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		skillId = buf.readResourceLocation();
		enabled = buf.readBoolean();
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		PlayerDataManager.handleDataSafely(ctx.getSender(), data ->
		{
			var skill = ImprovableSkills.SKILLS().getValue(skillId);
			if(skill != null) data.setSkillState(skill, enabled);
		});
	}
}