package org.unibl.etf.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.logs.ExceptionsLogger;

public class Configuration {
	private static String CONFIG_PATH = System.getProperty("user.home") + File.separator + "TokenServerApp" + File.separator + "config.properties";
	public static Logger logger;
	
	public Configuration() {
		File folder = new File(System.getProperty("user.home") + File.separator + "TokenServerApp");
		if(!folder.exists()) {
			folder.mkdir();
		}
		logger = new ExceptionsLogger().getLogger();
		if(checkIfFileExists()) {
			new File(CONFIG_PATH).delete();
		}
		try {
			createConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public boolean checkIfFileExists() {
		return new File(CONFIG_PATH).exists();
	}
	
	public void createConfigFile() throws IOException {
		File file = new File(CONFIG_PATH);
		file.createNewFile();
		
		FileOutputStream out = new FileOutputStream(file);
		Properties properties = new Properties();
		
		properties.setProperty("USERS_KEY", "users");
		properties.setProperty("TOKENS_KEY", "tokens");
		
		properties.store(out, null);
		out.close();
	}
	
	public static Properties readConfiguration() throws IOException {
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(new File(CONFIG_PATH));
		properties.load(in);
		in.close();		
		
		return properties;
	}
}
