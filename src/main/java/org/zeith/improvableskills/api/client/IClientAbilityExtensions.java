package org.zeith.improvableskills.api.client;

import org.zeith.improvableskills.api.registry.PlayerAbilityBase;

public interface IClientAbilityExtensions
{
	IClientAbilityExtensions DEFAULT = new IClientAbilityExtensions()
	{
	};
	
	static IClientAbilityExtensions of(PlayerAbilityBase a)
	{
		return a.getRenderPropertiesInternal() instanceof IClientAbilityExtensions e ? e : DEFAULT;
	}
	
	default ISlotRenderer slotRenderer()
	{
		return ISlotRenderer.NONE;
	}
}