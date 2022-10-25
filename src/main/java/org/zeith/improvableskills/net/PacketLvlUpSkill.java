package org.zeith.improvableskills.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketLvlUpSkill
		implements IPacket
{
	public ResourceLocation skill;
	
	public PacketLvlUpSkill(PlayerSkillBase skill)
	{
		this.skill = skill.getRegistryName();
	}
	
	public PacketLvlUpSkill()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(skill);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		skill = buf.readResourceLocation();
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		ServerPlayer player = ctx.getSender();
		
		PlayerDataManager.handleDataSafely(player, data ->
		{
			PlayerSkillBase skill = ImprovableSkills.SKILLS().getValue(this.skill);
			if(skill == null) return;
			
			short lvl = data.getSkillLevel(skill);
			if(skill.canUpgrade(data) && lvl < Short.MAX_VALUE - 1)
			{
				data.setSkillLevel(skill, lvl + 1);
				skill.onUpgrade(lvl, (short) (lvl + 1), data);
				PacketSyncSkillData.sync(player);
			}
		});
	}
}