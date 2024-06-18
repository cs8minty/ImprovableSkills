package org.zeith.improvableskills.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.zeith.improvableskills.ImprovableSkills;

public interface TagsIS3
{
	static void init()
	{
		Blocks.init();
		EntityTypes.init();
	}
	
	interface EntityTypes
	{
		private static void init()
		{
		}
		
		TagKey<EntityType<?>> PREVENT_COWBOY_INTERACTION = tag("prevent_cowboy_interaction");
		
		private static TagKey<EntityType<?>> tag(String name)
		{
			return TagKey.create(Registries.ENTITY_TYPE, ImprovableSkills.id(name));
		}
	}
	
	interface Blocks
	{
		private static void init()
		{
		}
		
		TagKey<Block> GROWTH_SKILL_BLOCKLIST = tag("growth_skill_blocklist");
		
		private static TagKey<Block> tag(String name)
		{
			return BlockTags.create(ImprovableSkills.id(name));
		}
	}
}