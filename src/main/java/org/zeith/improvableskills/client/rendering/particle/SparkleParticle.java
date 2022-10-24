package org.zeith.improvableskills.client.rendering.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.improvableskills.custom.particles.ParticleDataSparkle;

public class SparkleParticle
		extends DustParticleBase<ParticleDataSparkle>
{
	protected SparkleParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ParticleDataSparkle data, SpriteSet sprites)
	{
		super(level, x, y, z, xd, yd, zd, data, sprites);
		this.lifetime = data.getAge();
		this.friction = 0.98F;
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	protected float randomizeColor(float color, float factor)
	{
		return color;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider
			implements ParticleProvider<ParticleDataSparkle>
	{
		private final SpriteSet sprites;
		
		public Provider(SpriteSet sprites)
		{
			this.sprites = sprites;
		}
		
		@Override
		public Particle createParticle(ParticleDataSparkle data, ClientLevel level, double x, double y, double z, double xd, double yd, double zd)
		{
			return new SparkleParticle(level, x, y, z, xd, yd, zd, data, this.sprites);
		}
	}
}