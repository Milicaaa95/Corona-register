package model.forms;

import controller.AppUsageController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppUsageForm {
	private String jmb;
	
	public AppUsageForm(String jmb) {
		this.jmb = jmb;
	}
	
	public void display() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/app_usage.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Korištenje aplikacije");
        Scene scena = new Scene(loader.load());
        stage.setScene(scena);
        stage.setMinHeight(760);
        stage.setMinWidth(450);
        AppUsageController controller = loader.getController();
        controller.initialize(stage, jmb);
        stage.showAndWait();
    }
}
