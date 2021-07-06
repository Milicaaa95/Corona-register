package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.model.Person;
import org.unibl.etf.service.TokenService;
import org.unibl.etf.service.TokenServiceServiceLocator;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.AlertDisplaying;
import model.HashUtil;
import model.forms.ApplicationForm;
import model.forms.MainForm;

public class LogInController {
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private Person person;
    private Logger logger = ApplicationForm.logger;

    @FXML
    public void initialize(Stage stage, Person person) {
        this.stage = stage;
        this.person = person;
    }

    public void logIn() {
    	if(!"".equals(passwordField.getText())) {
			try {
				TokenServiceServiceLocator locator = new TokenServiceServiceLocator();
				TokenService service = locator.getTokenService();
				String password = service.getPassword(person);
				if(password != null && password.equals(HashUtil.getSHA256(passwordField.getText()))) {
					new MainForm(person).display();
					close();
				} else if(password == null) {
					person.setHashPassword(HashUtil.getSHA256(passwordField.getText()));
					if(service.updatePerson(person)) {
						new MainForm(person).display();
						close();
					}
				} else {
					AlertDisplaying.displayAlert("Unesena lozinka nije ispravna!");
				}
				
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			}
			
	    	} else {
	    	AlertDisplaying.displayAlert("Niste unijeli lozinku!");
	    }
    }
    
    private void close() {
    	stage.close();
    }
}
