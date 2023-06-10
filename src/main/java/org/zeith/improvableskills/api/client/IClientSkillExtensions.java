package org.zeith.improvableskills.api.client;

import org.zeith.improvableskills.api.registry.PlayerSkillBase;

public interface IClientSkillExtensions
{
	IClientSkillExtensions DEFAULT = new IClientSkillExtensions()
	{
	};
	
	static IClientSkillExtensions of(PlayerSkillBase s)
	{
		return s.getRenderPropertiesInternal() instanceof IClientSkillExtensions e ? e : DEFAULT;
	}
	
	default ISlotRenderer slotRenderer()
	{
		return ISlotRenderer.NONE;
	}
}