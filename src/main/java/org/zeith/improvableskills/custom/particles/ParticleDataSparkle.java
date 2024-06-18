package org.zeith.improvableskills.custom.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.zeith.improvableskills.init.ParticleTypesIS;

@Getter
public class ParticleDataSparkle
		extends ScalableParticleOptionsBase
{
	public static final MapCodec<ParticleDataSparkle> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(ParticleDataSparkle::getColor),
					Codec.FLOAT.fieldOf("scale").forGetter(ParticleDataSparkle::getScale),
					Codec.INT.fieldOf("age").forGetter(ParticleDataSparkle::getAge)
			).apply(instance, ParticleDataSparkle::new)
	);
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ParticleDataSparkle> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F, ParticleDataSparkle::getColor,
			ByteBufCodecs.FLOAT, ParticleDataSparkle::getScale,
			ByteBufCodecs.INT, ParticleDataSparkle::getAge,
			ParticleDataSparkle::new
	);
	
	private final int age;
	private final Vector3f color;
	
	public ParticleDataSparkle(Vector3f color, float scale, int age)
	{
		super(scale);
		this.color = color;
		this.age = age;
	}
	
	@Override
	public ParticleType<ParticleDataSparkle> getType()
	{
		return ParticleTypesIS.SPARKLE;
	}
}