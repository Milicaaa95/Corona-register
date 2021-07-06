package org.unibl.etf.logs;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.unibl.etf.configuration.Configuration;

public class ExceptionsLogger {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ExceptionsLogger() {
		try {
			Properties properties = Configuration.readParameters();
			String path = properties.getProperty("LOGS_PATH");
			
			FileHandler fileHandler = new FileHandler(path, true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}	
}
