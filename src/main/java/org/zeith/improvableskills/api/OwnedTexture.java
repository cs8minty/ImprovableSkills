package org.zeith.improvableskills.api;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.improvableskills.api.registry.IHasRegistryName;

public class OwnedTexture<V extends IHasRegistryName>
{
	public final V owner;
	public ResourceLocation texNorm, texHov;
	
	public OwnedTexture(V owner)
	{
		this.owner = owner;
	}
	
	public V owner()
	{
		return owner;
	}
	
	public UV toUV(boolean hovered)
	{
		if(texHov == null || texNorm == null)
		{
			var res = owner.getRegistryName();
			var sub = owner.textureFolder();
			this.texNorm = Resources.location(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_normal.png");
			this.texHov = Resources.location(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_hovered.png");
		}
		
		return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256);
	}
}