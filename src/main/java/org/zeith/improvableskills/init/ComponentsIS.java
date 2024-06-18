package org.zeith.improvableskills.init;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.improvableskills.api.PlayerSkillData;

@SimplyRegister
public interface ComponentsIS
{
	@RegistryName("skill_data")
	AttachmentType<PlayerSkillData> SKILL_DATA = AttachmentType.serializable(holder ->
	{
		if(holder instanceof Player pl)
			return new PlayerSkillData(pl);
		return null;
	}).copyOnDeath().copyHandler((attachment, holder, provider) ->
	{
		if(holder instanceof Player pl)
		{
			var psd = new PlayerSkillData(pl);
			psd.deserializeNBT(provider, attachment.serializeNBT(provider));
			return psd;
		}
		return null;
	}).build();
}