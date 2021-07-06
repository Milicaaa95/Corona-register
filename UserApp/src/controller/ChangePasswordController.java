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

public class ChangePasswordController {

	@FXML
	private PasswordField newPasswordField;
	
	private Stage stage;
	private Person person;
	private Logger logger = ApplicationForm.logger;
	
	@FXML
	public void initialize(Stage stage, Person person) {
		this.stage = stage;
		this.person = person;
	}
	
	public void save() {
		if(!"".equals(newPasswordField.getText())) {
			try {
				TokenServiceServiceLocator locator = new TokenServiceServiceLocator();
				TokenService service = locator.getTokenService();
				person.setHashPassword(HashUtil.getSHA256(newPasswordField.getText()));
				if(service.updatePerson(person)) {
					AlertDisplaying.displayAlert("Lozinka uspješno promijenjena.");
				}
				
				stage.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			}
			
		}
	}
	
}
