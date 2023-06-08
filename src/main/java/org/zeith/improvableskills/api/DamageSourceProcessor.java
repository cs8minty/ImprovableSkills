package org.zeith.improvableskills.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.zeith.hammerlib.util.java.Cast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DamageSourceProcessor
{
	@Nullable
	public static Player getAttackerAsPlayer(DamageSource src)
	{
		return Cast.cast(src != null ? src.getEntity() : null, Player.class);
	}
	
	@Nonnull
	public static DamageType getDamageType(DamageSource src)
	{
		if(src == null)
			return DamageType.UNKNOWN;
		for(DamageType t : DamageType.TYPES)
			if(t.isThisType(src))
				return t;
		return DamageType.UNKNOWN;
	}
	
	public static boolean isMinionEntity(Entity ent)
	{
		return getMinionOwner(ent) != null;
	}
	
	public static boolean isAlchemicalEntity(Entity ent)
	{
		return getAlchemicalOwner(ent) != null;
	}
	
	public static boolean isRangedDamage(DamageSource src)
	{
		return getRangedOwner(src) != null;
	}
	
	public static Player getMinionOwner(Entity ent)
	{
		if(ent instanceof OwnableEntity && ((OwnableEntity) ent).getOwner() instanceof Player pl)
			return pl;
		return null;
	}
	
	public static Player getAlchemicalOwner(Entity ent)
	{
		if(ent instanceof ThrownPotion && ((ThrownPotion) ent).getOwner() instanceof Player pl)
			return pl;
		return null;
	}
	
	public static Player getRangedOwner(DamageSource ds)
	{
		/* Check that this isn't alchemical damage */
		if(getAlchemicalOwner(ds.getDirectEntity()) != null)
			return null;
		
		if(ds.getDirectEntity() instanceof Projectile && ds.getEntity() instanceof Player pl)
			return pl;
		
		return null;
	}
	
	public static Player getMeleeAttacker(DamageSource ds)
	{
		if(getDamageType(ds) == DamageType.MELEE)
			return Cast.cast(ds.getEntity(), Player.class);
		
		return null;
	}
	
	public static class DamageType
	{
		private static final List<DamageType> TYPES = new ArrayList<>();
		private static DamageType[] arTypes;
		
		public static final DamageType MELEE = new DamageType(d -> d.getEntity() == d.getDirectEntity() && d.getEntity() instanceof Player),
				RANGED = new DamageType(DamageSourceProcessor::isRangedDamage),
				MINION = new DamageType(d -> isMinionEntity(d.getEntity())),
				// MAGIC = new DamageType(d -> !isAlchemicalEntity(d.getDirectEntity()) && d.isMagic()),
				ALCHEMICAL = new DamageType(d -> isAlchemicalEntity(d.getDirectEntity())),
				UNKNOWN = new DamageType(d -> false);
		
		private final Predicate<DamageSource> test;
		
		public DamageType(Predicate<DamageSource> test)
		{
			this.test = test;
		}
		
		{
			TYPES.add(this);
			arTypes = TYPES.toArray(new DamageType[0]);
		}
		
		public boolean isThisType(DamageSource src)
		{
			return test.test(src);
		}
		
		/**
		 * Don't be silly -- DO NOT EDIT THIS ARRAY
		 */
		public static DamageType[] getTypes()
		{
			return arTypes;
		}
	}
}