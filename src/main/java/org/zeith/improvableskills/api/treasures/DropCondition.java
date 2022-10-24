package org.zeith.improvableskills.api.treasures;

@FunctionalInterface
public interface DropCondition
{
	boolean canDrop(TreasureContext ctx);
}