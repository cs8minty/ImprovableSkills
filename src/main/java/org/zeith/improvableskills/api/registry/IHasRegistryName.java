package org.zeith.improvableskills.api.registry;

import net.minecraft.resources.ResourceLocation;

public interface IHasRegistryName
{
	ResourceLocation getRegistryName();
	
	String textureFolder();
}