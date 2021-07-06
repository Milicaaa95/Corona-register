package org.unibl.etf.logs;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ExceptionsLogger {
	private final String LOGS_PATH = System.getProperty("user.home") + File.separator + "TokenServerApp" + File.separator + "errors.log";
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ExceptionsLogger() {
		File folder =  new File(System.getProperty("user.home") + File.separator + "TokenServerApp");
		if(!folder.exists()) {
			folder.mkdir();
		}
		try {
			FileHandler fileHandler = new FileHandler(LOGS_PATH, true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			
		}
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
