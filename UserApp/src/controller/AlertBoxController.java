package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.forms.AlertBoxForm;
import model.forms.ApplicationForm;

public class AlertBoxController {

    @FXML
    private Label alertLabel;
    @FXML
    private Button okButton;

    private Stage stage;
    private Logger logger = ApplicationForm.logger;

    public void initialize(Stage stage) {
        try {
            this.stage = stage;
            alertLabel.setText(AlertBoxForm.text);
        } catch(Exception ex) {
        	logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
        }
    }

    public void ok() {
       stage.close();
    }

}
