package controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MapController {
	
	@FXML
	private GridPane mapGridPane;
	@FXML
	private Label messageLabel;
	
	@FXML
	public void initialize(Stage stage, List<Integer> positions, String message) {
		
		if(message != null) {
			messageLabel.setText(message);
		}
		
		int rows = positions.get(0);
		int columns = positions.get(1);
		
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				Label label = new Label(String.format("%2s", ""));
				label.setAlignment(Pos.CENTER);
				mapGridPane.add(label, column, row);
			}
		}
		
		for(int i = 2; i < positions.size(); i += 2) {
			Label label = new Label("*");
			label.setAlignment(Pos.CENTER);
			mapGridPane.add(label, positions.get(i + 1), positions.get(i));
			
		}
	}
	
}
