package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	public static String CONFIG_PATH = "config" + File.separator + "config.properties";
	
	public static void writeConfiguration() throws IOException {
		File folder = new File("config");
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		File file = new File(CONFIG_PATH);
		file.createNewFile();
		
		Properties properties = new Properties();
		properties.setProperty("CHAT_PORT", "8443");
		properties.setProperty("KEY_STORE_PATH", "keystore.jks");
		properties.setProperty("KEY_STORE_PASS", "mdppass2020");
		properties.setProperty("ADDRESS", "127.0.0.1");
		properties.setProperty("FILE_PORT", "1099");
		properties.setProperty("POLICY_FILE", "client_policyfile.txt");
		properties.setProperty("MULTICAST_PORT", "9990");
		properties.setProperty("MULTICAST_ADDRESS", "224.0.0.11");
		properties.setProperty("SERIALIZATION_PATH", "serialization");
		properties.setProperty("LOGS_PATH", "error.log");
		properties.setProperty("DOCUMENTS_PATH", "dokumenti_pacijenata");
		
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
	
	public static boolean checkIfFileExists() {
		return new File(CONFIG_PATH).exists();
	}
	
	
}
