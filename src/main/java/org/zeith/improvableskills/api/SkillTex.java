package org.zeith.improvableskills.api;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.improvableskills.api.registry.IHasRegistryName;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;

public class SkillTex<V extends IHasRegistryName>
{
	public final V owner;
	public ResourceLocation texNorm, texHov;
	
	public SkillTex(V owner)
	{
		this.owner = owner;
	}
	
	public UV toUV(boolean hovered)
	{
		if(texHov == null || texNorm == null)
		{
			ResourceLocation res = owner.getRegistryName();
			
			String sub = "skills";
			if(owner instanceof PlayerAbilityBase)
				sub = "abilities";
			
			this.texNorm = new ResourceLocation(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_normal.png");
			this.texHov = new ResourceLocation(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_hovered.png");
		}
		
		return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256);
	}
}