package org.zeith.improvableskills.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.zeith.hammerlib.net.lft.ITransportAcceptor;
import org.zeith.hammerlib.net.lft.TransportSessionBuilder;
import org.zeith.improvableskills.ImprovableSkills;

import java.io.*;

public class NetSkillCalculator
		implements ITransportAcceptor
{
	@Override
	public void read(InputStream readable, int length)
	{
		try
		{
			CompoundTag nbt = NbtIo.readCompressed(readable);
			
			ImprovableSkills.SKILLS()
					.forEach(skill -> skill.xpCalculator.readClientNBT(nbt.getCompound("SkillCost" + skill.getRegistryName().toString())));
			
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
		
		ImprovableSkills.SKILLS().forEach(skill ->
		{
			CompoundTag tag = new CompoundTag();
			skill.xpCalculator.writeServerNBT(tag);
			nbt.put("SkillCost" + skill.getRegistryName().toString(), tag);
		});
		
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