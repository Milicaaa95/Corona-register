package controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.AppUsage;
import model.forms.ApplicationForm;
import service.PersonService;

public class AppUsageController {
	@FXML
	private TableView<AppUsage> appUsageTableView;
	
	private Stage stage;
	private Logger logger = ApplicationForm.logger;
	private String jmb;
	
	@FXML
	public void initialize(Stage stage, String jmb) {
		this.stage = stage;
		this.jmb = jmb;
		
		appUsageTableView.getColumns().get(0).setStyle("-fx-alignment: CENTER;");
		appUsageTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("logInTime"));
		appUsageTableView.getColumns().get(1).setStyle("-fx-alignment: CENTER;");
		appUsageTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("logOutTime"));
		appUsageTableView.getColumns().get(2).setStyle("-fx-alignment: CENTER;");
		appUsageTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("sessionDuration"));
		displayUsages();
	}
	
	public void ok() {
		stage.close();
	}
	
	//prikaz svih korištenja aplikacije po jmb-u osobe
	private void displayUsages() {
		try {
			List<AppUsage> usages = PersonService.getUsages(jmb);
			if(usages.size() > 0) {
				appUsageTableView.getItems().addAll(usages);
			}
		} catch(Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
}
