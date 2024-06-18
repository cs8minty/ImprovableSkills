package org.zeith.improvableskills.net;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import org.zeith.hammerlib.net.lft.ITransportAcceptor;
import org.zeith.hammerlib.net.lft.TransportSessionBuilder;
import org.zeith.improvableskills.ImprovableSkills;
import org.zeith.improvableskills.api.registry.PlayerSkillBase;

import java.io.*;
import java.util.function.Supplier;

public class NetSkillCalculator
		implements ITransportAcceptor
{
	@Override
	public void read(InputStream readable, int length, Supplier<RegistryAccess> registryAccess)
	{
		try
		{
			CompoundTag nbt = NbtIo.readCompressed(readable, NbtAccounter.unlimitedHeap());
			
			for(var skill : ImprovableSkills.SKILLS)
				skill.xpCalculator.readClientNBT(nbt.getCompound("SkillCost" + skill.getRegistryName().toString()));
			
			ImprovableSkills.LOG.info("Received server settings.");
		} catch(Throwable err)
		{
			err.printStackTrace();
		}
	}
	
	public static TransportSessionBuilder pack()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompoundTag nbt = new CompoundTag();
		
		for(PlayerSkillBase skill : ImprovableSkills.SKILLS)
		{
			CompoundTag tag = new CompoundTag();
			skill.xpCalculator.writeServerNBT(tag);
			nbt.put("SkillCost" + skill.getRegistryName().toString(), tag);
		}
		
		try
		{
			NbtIo.writeCompressed(nbt, baos);
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return new TransportSessionBuilder().setAcceptor(NetSkillCalculator.class).addData(baos.toByteArray());
	}
}