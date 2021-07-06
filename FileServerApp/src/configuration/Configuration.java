package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static String CONFIG_PATH = "config" + File.separator + "config.properties";
	
	public static boolean checkIfFileExists() {
		return new File(CONFIG_PATH).exists();
	}
	
	public static void writeConfig() throws IOException {
		File folder = new File("config");
		if(!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(CONFIG_PATH);
		file.createNewFile();
		
		Properties properties = new Properties();
		properties.setProperty("PATH", "files");
		properties.setProperty("FILE_PORT", "1099");
		properties.setProperty("POLICY_FILE", "server_policyfile.txt");
		properties.setProperty("LOGS_PATH", "error.log");
		
		FileOutputStream out = new FileOutputStream(file);
		properties.store(out, null);
		
		out.close();
	}
	
	public static Properties readParameters() throws IOException {	
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(new File(CONFIG_PATH));
		properties.load(in);
		in.close();		
		
		return properties;
	}
}
