package org.zeith.improvableskills.custom.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ParticleTypeWithCodec<T extends ParticleOptions>
		extends ParticleType<T>
{
	private final Codec<T> codec;
	
	public ParticleTypeWithCodec(boolean overrideLimiter, Codec<T> codec, ParticleOptions.Deserializer<T> deserializer)
	{
		super(overrideLimiter, deserializer);
		this.codec = codec;
	}
	
	@Override
	public Codec<T> codec()
	{
		return codec;
	}
}