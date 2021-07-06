package model.forms;

import controller.SignUpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignUpForm {

	public void display() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/sign_up.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Registracija na token server");
        stage.setScene(scene);
        stage.setMinHeight(450);
        stage.setMinWidth(250);
        SignUpController controller = loader.getController();
        controller.initialize(stage);
        stage.show();
      }
}
