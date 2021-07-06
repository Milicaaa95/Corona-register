package model.forms;

import javafx.application.Application;
import javafx.stage.Stage;
import logs.ExceptionsLogger;

import java.util.logging.Logger;

import configuration.Configuration;

public class ApplicationForm extends Application {
	public static Logger logger;

    @Override
    public void start(Stage stage) throws Exception {
    	if(!Configuration.checkIfFileExists()) {
    		Configuration.writeConfiguration();
    	}
    	logger = new ExceptionsLogger().getLogger();
    	new SignUpForm().display();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
