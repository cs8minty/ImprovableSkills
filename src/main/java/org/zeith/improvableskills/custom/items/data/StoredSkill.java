package org.zeith.improvableskills.custom.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

@SimplyRegister
public record StoredSkill(PlayerSkillBase skill)
{
	public static final Codec<StoredSkill> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					ImprovableSkills.SKILLS.byNameCodec().fieldOf("ability").forGetter(StoredSkill::skill)
			).apply(inst, StoredSkill::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, StoredSkill> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC.map(ImprovableSkills.SKILLS::get, ImprovableSkills.SKILLS::getKey), StoredSkill::skill,
			StoredSkill::new
	);
	
	@RegistryName("stored_skill")
	public static final Registrar<DataComponentType<StoredSkill>> TYPE = Registrar.dataComponentType(b -> b
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.cacheEncoding()
	);
}