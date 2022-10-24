package org.zeith.improvableskills.data;

import net.minecraftforge.fml.loading.FMLPaths;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.improvableskills.ImprovableSkills;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ClientData
{
	public static Optional<String> readData(String sub)
	{
		var file = getModDataPath().resolve(sub).toFile();
		if(file.isFile()) return Optional.ofNullable(tryFileRead(file));
		return Optional.empty();
	}
	
	public static void writeData(String sub, String data)
	{
		var file = getModDataPath().resolve(sub).toFile();
		tryFileWrite(file, data);
	}
	
	public static Path getModDataPath()
	{
		var path = FMLPaths.GAMEDIR.get()
				.resolve(HLConstants.MOD_ID)
				.resolve(ImprovableSkills.MOD_ID);
		try
		{
			Files.createDirectories(path);
		} catch(IOException e)
		{
		}
		return path;
	}
	
	private static String tryFileRead(File file)
	{
		try
		{
			return file.isFile() ? Files.readString(file.toPath()) : null;
		} catch(Exception e)
		{
			return null;
		}
	}
	
	private static void tryFileWrite(File file, String str)
	{
		try
		{
			Files.writeString(file.toPath(), str);
		} catch(Exception e)
		{
		}
	}
	
	
}