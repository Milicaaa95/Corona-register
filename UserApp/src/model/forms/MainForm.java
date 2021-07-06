package model.forms;

import org.unibl.etf.model.Person;

import controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForm {
	
	private Person person;
	
	public MainForm(Person person) {
		this.person = person;
	}
	
	public void display() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/main.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Pacijenti - glavna forma");
        stage.setScene(scene);
        stage.setMinHeight(900);
        stage.setMinWidth(600);
        MainController controller = loader.getController();
        controller.initialize(stage, person);
        stage.show();
     }

}
