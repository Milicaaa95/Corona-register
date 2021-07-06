package model.forms;

import java.util.List;

import controller.MapController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MapForm {
	private List<Integer> positions;
	
	public MapForm(List<Integer> positions) {
		this.positions = positions;
	}
	public void display() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../view/map.fxml"));
        Stage stage = new Stage();
        stage.setResizable(false);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Mapa");
        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(400);
        MapController controller = loader.getController();
        controller.initialize(stage, positions);
        stage.show();
	}
}
