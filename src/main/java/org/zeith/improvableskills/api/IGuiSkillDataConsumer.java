package org.zeith.improvableskills.api;

@FunctionalInterface
public interface IGuiSkillDataConsumer
{
	void applySkillData(PlayerSkillData data);
}