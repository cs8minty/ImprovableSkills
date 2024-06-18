package org.zeith.improvableskills.custom.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTypeWithCodec<T extends ParticleOptions>
		extends ParticleType<T>
{
	private final MapCodec<T> codec;
	private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
	
	public ParticleTypeWithCodec(boolean overrideLimiter, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> deserializer)
	{
		super(overrideLimiter);
		this.codec = codec;
		this.streamCodec = streamCodec();
	}
	
	@Override
	public MapCodec<T> codec()
	{
		return codec;
	}
	
	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec()
	{
		return streamCodec;
	}
}