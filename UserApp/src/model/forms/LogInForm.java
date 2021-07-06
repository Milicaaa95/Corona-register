package model.forms;

import org.unibl.etf.model.Person;

import configuration.Configuration;
import controller.LogInController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogInForm {
	
	private Person person;
	
	public LogInForm(Person person) {
		this.person = person;
	}

    public void display() throws Exception {
    	if(!Configuration.checkIfFileExists()) {
    		Configuration.writeConfiguration();
    	}
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/log_in.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Prijava");
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(270);
        LogInController controller = loader.getController();
        controller.initialize(stage, person);
        stage.show();
    }
}
