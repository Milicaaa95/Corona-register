package model.forms;

import java.util.ArrayList;

import controller.FileController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FileForm {
	private String text;
	private ArrayList<byte[]> data;
	private String uuid;
	
	public FileForm(String text, ArrayList<byte[]> data, String uuid) {
		this.text = text;
		this.data = data;
		this.uuid = uuid;
	}

	public void display() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/file.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Pregled fajla");
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(300);
        FileController controller = loader.getController();
        controller.initialize(stage, text, data, uuid);
        stage.showAndWait();
	}
}
