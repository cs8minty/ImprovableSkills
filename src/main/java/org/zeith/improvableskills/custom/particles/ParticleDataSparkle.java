package org.zeith.improvableskills.custom.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.zeith.improvableskills.init.ParticleTypesIS;

public class ParticleDataSparkle
		extends DustParticleOptionsBase
{
	public static final Codec<ParticleDataSparkle> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(ParticleDataSparkle::getColor),
					Codec.FLOAT.fieldOf("scale").forGetter(ParticleDataSparkle::getScale),
					Codec.INT.fieldOf("age").forGetter(ParticleDataSparkle::getAge)
			).apply(instance, ParticleDataSparkle::new)
	);
	
	public static final ParticleOptions.Deserializer<ParticleDataSparkle> DESERIALIZER = new Deserializer<>()
	{
		@Override
		public ParticleDataSparkle fromCommand(ParticleType<ParticleDataSparkle> type, StringReader reader) throws CommandSyntaxException
		{
			var color = readVector3f(reader);
			reader.expect(' ');
			float scale = reader.readFloat();
			reader.expect(' ');
			int age = reader.readInt();
			return new ParticleDataSparkle(color, scale, age);
		}
		
		@Override
		public ParticleDataSparkle fromNetwork(ParticleType<ParticleDataSparkle> type, FriendlyByteBuf buffer)
		{
			var color = readVector3f(buffer);
			float scale = buffer.readFloat();
			int age = buffer.readInt();
			return new ParticleDataSparkle(color, scale, age);
		}
	};
	
	private final int age;
	
	public ParticleDataSparkle(Vector3f color, float scale, int age)
	{
		super(color, scale);
		this.age = age;
	}
	
	@Override
	public ParticleType<ParticleDataSparkle> getType()
	{
		return ParticleTypesIS.SPARKLE;
	}
	
	public int getAge()
	{
		return age;
	}
}