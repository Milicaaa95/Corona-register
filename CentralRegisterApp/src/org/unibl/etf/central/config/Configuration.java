package org.unibl.etf.central.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.unibl.etf.central.util.RestUtil;

public class Configuration {
	private static String CONFIG_PATH = System.getProperty("user.home") + File.separator + "CentralRegisterApp" + File.separator + "config.properties";
	
	public Configuration() {
		File folder = new File(System.getProperty("user.home") + File.separator + "CentralRegisterApp");
		if(!folder.exists()) {
			folder.mkdir();
		}
		if(checkIfFileExists()) {
			new File(CONFIG_PATH).delete();
		}
		try {
			createConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
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
		
		properties.setProperty("rows", "50");
		properties.setProperty("columns", "50");
		properties.setProperty("seconds", "5");
		properties.setProperty("distance", "2");
		properties.setProperty("interval", "10");
		properties.setProperty("INFECTED_PERSONS", "infected");
		properties.setProperty("POTENTIALLY_INFECTED_PERSONS", "potentially_infected");
		properties.setProperty("NOT_INFECTED_PERSONS", "not_infected");
		properties.setProperty("MAP_KEY", "map");
		properties.setProperty("BLOCKED_PERSONS", "blocked");
		
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
	
	public ArrayList<Integer> getMapDimensions() throws IOException {
		ArrayList<Integer> dimensions = new ArrayList<>();
		FileInputStream in = new FileInputStream(new File(CONFIG_PATH));
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		
		dimensions.add(Integer.valueOf(properties.getProperty("rows")));
		dimensions.add(Integer.valueOf(properties.getProperty("columns")));
		
		return dimensions;
	}
}
