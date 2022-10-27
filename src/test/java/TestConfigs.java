import org.zeith.hammerlib.util.configured.ConfigFile;

import java.io.File;

public class TestConfigs
{
	public static void main(String[] args)
	{
		File file = new File("run/client/config/improvableskills.cfg");
		ConfigFile cfg = new ConfigFile(file);
		System.out.println(cfg);
	}
}