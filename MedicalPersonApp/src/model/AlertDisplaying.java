package model;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.forms.AlertBoxForm;
import model.forms.ApplicationForm;

public class AlertDisplaying {

	private static Logger logger = ApplicationForm.logger;
	
	public static void displayAlert(String content) {
		try {
			new AlertBoxForm(content).display();
		} catch(Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
}
