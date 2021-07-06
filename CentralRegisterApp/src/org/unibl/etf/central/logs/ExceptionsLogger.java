package org.unibl.etf.central.logs;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ExceptionsLogger {	
	private String PATH = System.getProperty("user.home") + File.separator + "CentralRegisterApp" + File.separator + "errors.log";
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ExceptionsLogger() {
		File folder =  new File(System.getProperty("user.home") + File.separator + "CentralRegisterApp");
		if(!folder.exists()) {
			folder.mkdir();
		}
		try {
			FileHandler fileHandler = new FileHandler(PATH, true);
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
