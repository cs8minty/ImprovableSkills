package org.zeith.improvableskills.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;
import org.zeith.improvableskills.data.PlayerDataManager;

@MainThreaded
public class PacketLvlDownSkill
		implements IPacket
{
	public ResourceLocation skill;
	
	public PacketLvlDownSkill(PlayerSkillBase skill)
	{
		this.skill = skill.getRegistryName();
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
			PlayerSkillBase skill = ImprovableSkills.SKILLS.get(this.skill);
			if(skill == null) return;
			
			short lvl = data.getSkillLevel(skill);
			if(lvl > 0 && skill.isDowngradable(data))
			{
				data.setSkillLevel(skill, lvl - 1);
				skill.onUpgrade(lvl, (short) (lvl - 1), data);
				skill.onDowngrade(data, lvl);
				player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
				PacketSyncSkillData.sync(player);
			}
		});
	}
}