package model.forms;

import java.util.logging.Logger;

import configuration.Configuration;
import javafx.application.Application;
import javafx.stage.Stage;
import logs.ExceptionsLogger;

public class ApplicationForm extends Application {
	public static Logger logger;
	
	@Override
	public void start(Stage stage) throws Exception {
		if(!Configuration.checkIfFileExists()) {
			Configuration.writeConfiguration();
		}
		
		logger = new ExceptionsLogger().getLogger();
		
		new MainForm().display();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
