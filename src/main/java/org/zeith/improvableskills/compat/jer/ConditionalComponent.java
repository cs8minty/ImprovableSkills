package org.zeith.improvableskills.compat.jer;

import jeresources.api.conditionals.Conditional;
import net.minecraft.network.chat.Component;

public class ConditionalComponent
		extends Conditional
{
	public final Component par;
	
	public ConditionalComponent(Component par)
	{
		this.par = par;
	}
	
	@Override
	public Component toStringTextComponent()
	{
		return par;
	}
}