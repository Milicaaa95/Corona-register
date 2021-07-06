package model.forms;

import org.unibl.etf.model.Person;

import controller.ChangePasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChangePasswordForm {
	
	private Person person;
	
	public ChangePasswordForm(Person person) {
		this.person = person;
	}
	
	public void display() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/change_password.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Promjena lozinke");
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(270);
        ChangePasswordController controller = loader.getController();
        controller.initialize(stage, person);
        stage.show();
    }

}
