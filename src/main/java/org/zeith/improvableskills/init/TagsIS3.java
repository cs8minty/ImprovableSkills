package org.zeith.improvableskills.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.zeith.improvableskills.ImprovableSkills;

public class TagsIS3
{
	public static void init()
	{
		Blocks.init();
	}
	
	public static class Blocks
	{
		private static void init()
		{
		}
		
		public static final TagKey<Block> GROWTH_SKILL_BLOCKLIST = tag("growth_skill_blocklist");
		
		private static TagKey<Block> tag(String name)
		{
			return BlockTags.create(new ResourceLocation(ImprovableSkills.MOD_ID, name));
		}
	}
}