package model.forms;

import controller.AlertBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlertBoxForm {
	public static String text;

    public AlertBoxForm(String tekst) {
        AlertBoxForm.text = tekst;
    }

    public void display() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/alert_box.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Upozorenje");
        Scene scena = new Scene(loader.load());
        stage.setScene(scena);
        stage.setMinHeight(400);
        stage.setMinWidth(500);
        AlertBoxController controller = loader.getController();
        controller.initialize(stage);
        stage.showAndWait();
    }
}
