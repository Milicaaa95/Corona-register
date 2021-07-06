package model.forms;

import controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForm {

	public void display() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/main.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Medicinsko osoblje - glavna forma");
        stage.setScene(scene);
        stage.setMinHeight(850);
        stage.setMinWidth(600);
        MainController controller = loader.getController();
        controller.initialize(stage);
        stage.show();
    }

}
