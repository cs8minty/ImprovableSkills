package org.zeith.improvableskills.custom.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;

@SimplyRegister
public record StoredAbility(PlayerAbilityBase ability)
{
	public static final Codec<StoredAbility> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					ImprovableSkills.ABILITIES.byNameCodec().fieldOf("ability").forGetter(StoredAbility::ability)
			).apply(inst, StoredAbility::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, StoredAbility> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC.map(ImprovableSkills.ABILITIES::get, ImprovableSkills.ABILITIES::getKey), StoredAbility::ability,
			StoredAbility::new
	);
	
	@RegistryName("stored_ability")
	public static final Registrar<DataComponentType<StoredAbility>> TYPE = Registrar.dataComponentType(b -> b
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.cacheEncoding()
	);
}