package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AlertDisplaying;
import model.forms.ApplicationForm;
import model.forms.LogInForm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.model.Person;
import org.unibl.etf.service.TokenService;
import org.unibl.etf.service.TokenServiceServiceLocator;

public class SignUpController {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField jmbTextField;

    private Stage stage;
    private Logger logger = ApplicationForm.logger;

    @FXML
    public void initialize(Stage stage) {
        this.stage = stage;
    }

    public void signUp() {
    	if(checkNameTextField() && checkSurnameTextField() && checkJmbTextField()) {
            Person person = new Person(null, jmbTextField.getText(), nameTextField.getText(), surnameTextField.getText(), null);
            try {
	            TokenServiceServiceLocator loc = new TokenServiceServiceLocator();
	            TokenService service = loc.getTokenService();
	            String token = service.getToken(person);
	            if(token != null) {
	            	person.setUuid(token);
	            	new LogInForm(person).display();
	            }
            close();
            } catch(Exception ex) {
                logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
            }
    	}
    }

    private boolean checkNameTextField() {
        if("".equals(nameTextField.getText())) {
        	AlertDisplaying.displayAlert("Nije uneseno ime!");
            return false;
        }
        return true;
    }

    private boolean checkSurnameTextField() {
        if("".equals(surnameTextField.getText())) {
        	AlertDisplaying.displayAlert("Nije uneseno prezime!");
            return false;
        }
        return true;
    }

    private boolean checkJmbTextField() {
        if("".equals(jmbTextField.getText())) {
        	AlertDisplaying.displayAlert("Nije unesen jmb!");
            return false;
        }
        return true;
    }

    private void close() {
        stage.close();
    }
}
