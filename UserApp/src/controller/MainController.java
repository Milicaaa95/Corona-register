package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.model.Person;
import org.unibl.etf.service.TokenService;
import org.unibl.etf.service.TokenServiceServiceLocator;

import configuration.Configuration;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.AlertDisplaying;
import model.ChatClient;
import model.forms.AppUsageForm;
import model.forms.ApplicationForm;
import model.forms.ChangePasswordForm;
import model.forms.LogInForm;
import model.forms.MapForm;
import server.FileServerClient;
import server.ZipUtil;
import service.PersonService;

public class MainController {
    @FXML
    private Label alertLabel;
    @FXML
    private HBox detailsHBox;
    @FXML
    private Button detailsButton;
    @FXML
    private TextField xTextField;
    @FXML
    private TextField yTextField;
    @FXML
    private TextField fromTextField;
    @FXML
    private TextField toTextField;
    @FXML
    private MenuBar menu;
    @FXML
    private VBox chatVBox;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Button chooserButton;
    @FXML
    private VBox documentsVBox;
    
    private Stage stage;
    private Logger logger = ApplicationForm.logger;
    private Person person;
    public static String token = null;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
    private String dateFrom;
    private List<Integer> potentiallyInfectedPositions;
    private List<Integer> oldPotentiallyInfectedPositions;
    private List<Integer> infectedPositions;
    private List<Integer> oldInfectedPositions;
    private ChatClient chatClient;
    private File selectedFile;
    private FileServerClient fileServerClient;
    public static int numberOfDocuments = 0;
    private boolean active = true;
    private boolean disabled = false;
    
    @FXML
    public void initialize(Stage stage, Person person) {
    	this.stage = stage;
    	this.person = person;
    	fileServerClient = new FileServerClient();
    	messageTextField.setDisable(true);
    	sendButton.setDisable(true);
    	documentsVBox.getChildren().stream().forEach(e -> e.setDisable(true));
    	
    	dateFrom = formatter.format(new Date());

    	token = person.getUuid();
    	
        detailsHBox.setVisible(false);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				saveUsage();
				if(chatClient != null) {
					chatClient.finish();
				}
				active = false;
				
				try {
					Thread.sleep(500);
				} catch(InterruptedException ex) {
					logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
				}
				Platform.exit();
			}
		});
        
        monitorChanges();
    }

    public void viewAppUsage() {
    	if(!disabled) {
	    	try {
				new AppUsageForm(person.getJmb()).display();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
			}
    	} else {
    		AlertDisplaying.displayAlert("Nedozvoljena operacija.");
    	}
    }

    public void viewMap() {
    	if(!disabled) {
	    	try {
	    		int numberOfDays = Integer.valueOf(Configuration.readParameters().getProperty("DAYS"));
	    		List<Integer> positions = PersonService.getPositionsForUser(token, numberOfDays);
	    		if(positions != null) {
	    			new MapForm(positions, null).display();
	    		} 
	    	} catch(Exception ex) {
	    		logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
	    	}
    	} else {
			AlertDisplaying.displayAlert("Nedozvoljena operacija.");
		}
    }

    public void changePassword() {
    	try {
			new ChangePasswordForm(person).display();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
    }

    public void systemLogOut() {
    	try {
			new LogInForm(person).display();
			stage.close();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
    }

    public void registryLogOut() {
    	try {
			TokenServiceServiceLocator loc = new TokenServiceServiceLocator();
			TokenService service = loc.getTokenService();
			if(service.logOut(person)) {
				AlertDisplaying.displayAlert("Uspješna odjava iz registra!");
			}
			stage.close();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
    }
    
    public void startChat() {
    	messageTextField.setDisable(false);
    	sendButton.setDisable(false);
    	documentsVBox.setDisable(false);
    	chatClient = new ChatClient(chatVBox, messageTextField, sendButton, documentsVBox);
    	fileServerClient.start();
    	
    	Thread chatThread = new Thread(new Runnable() {
			@Override
			public void run() {
				chatClient.start();
				
			}
		});
    	chatThread.setDaemon(true);
    	chatThread.start();
    }
    
    //dodavanje lokacije i provjera validnosti svih unesenih parametara
    public void submit() {
    	Integer x = checkXCoordinate();
    	Integer y = checkYCoordinate();
    	Date from = checkDateFrom();
    	Date to = checkDateTo();
    	
    	if(x != null && y != null && from != null && to != null) {	
    		if(!disabled) {
    			if(PersonService.saveLocation(token, x, y, fromTextField.getText(), toTextField.getText())) {
        			AlertDisplaying.displayAlert("Uspješno sačuvana lokacija.");
        		} else {
        			AlertDisplaying.displayAlert("Desio se problem prilikom čuvanja lokacije.");
        		}
    		} else {
    			AlertDisplaying.displayAlert("Nedozvoljena operacija.");
    		}
    		
    	}
    	xTextField.clear();
    	yTextField.clear();
    	fromTextField.clear();
    	toTextField.clear();
    }
    
    public void sendMessage() {
    	if(checkMessageTextField()) {
    		chatClient.setMessage(messageTextField.getText());
    		messageTextField.clear();
    	}
    }

    public void selectDocument() {
    	FileChooser fileChooser;
    	if(numberOfDocuments < 6) {
	    	fileChooser = new FileChooser();
	    	selectedFile = fileChooser.showOpenDialog(chooserButton.getScene().getWindow());
	    	numberOfDocuments++;
    	} else {
    		AlertDisplaying.displayAlert("Ne možete više od 5 dokumenta poslati!");
    	}
    }
    
    //ako je dokument do 1MB šalje se kao normalan fajl, u suprtnom pravi se zip, pa se kao takav šalje na server
    public void sendDocument() {
    	if(selectedFile != null) {
    		try {
    			if(selectedFile.length() <= 1024 * 1024) {
    				sendFile(selectedFile, false);
    			} else {
    				File zipFile = ZipUtil.zip(selectedFile);
    				sendFile(zipFile, true);
    				zipFile.delete();
    			}
	    		selectedFile = null;
			} catch (FileNotFoundException ex) {
				logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
			} catch (IOException ex) {
				logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
			}
    	}
    }
    
    //da li je osoba zaražena ili potencijalno zaražena
    private void monitorChanges() {
    	
    	Runnable task = new Runnable() {
			@Override
			public void run() {
				while(active) {
					potentiallyInfectedPositions = PersonService.isPersonPotentiallyInfected(token);
					infectedPositions = PersonService.isPersonInfected(token);
					
					if(potentiallyInfectedPositions == null || infectedPositions == null) {
						active = false;
						disabled = true;
					} else if(!infectedPositions.equals(oldInfectedPositions) && infectedPositions.size() > 2) {
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								showDetails(infectedPositions, "Zaraženi ste!");								
							}
						});
						
						oldInfectedPositions = infectedPositions;
					} else if(!potentiallyInfectedPositions.equals(oldPotentiallyInfectedPositions) && potentiallyInfectedPositions.size() > 2) {
			    		Platform.runLater(new Runnable() {
							@Override
							public void run() {
								detailsHBox.setVisible(true);
					    		alertLabel.setText("Potencijalno ste zaraženi!");
								detailsButton.setOnAction(e -> {
					    			showDetails(potentiallyInfectedPositions, null);
					    		});
								oldPotentiallyInfectedPositions = potentiallyInfectedPositions;
							}
						});
			    		
			    	} else {
			    		detailsHBox.setVisible(false);
			    	}
					try {
				    	Thread.sleep(5000);
				    } catch(Exception ex) {
				    	logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
				    }
		    	}
				
			}
		};
		
		Thread monitorThread = new Thread(task);
		monitorThread.setDaemon(true);
		monitorThread.start();
    	
    }
    
    private Integer checkXCoordinate() {
    	if(!"".equals(xTextField.getText())) {
    		try {
    			 int x = Integer.parseInt(xTextField.getText());
    			 return x;
    		} catch(NumberFormatException ex) {
    			try {
    				AlertDisplaying.displayAlert("Unos za polje koordinate x nije odgovarajući.");
    				return null;
    			} catch(Exception e) {
    				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
    			}
    		}
    	}
    	AlertDisplaying.displayAlert("Niste unijeli x koordinatu.");
    	return null;
    }
    
    private Integer checkYCoordinate() {
    	if(!"".equals(yTextField.getText())) {
    		try {
    			int y = Integer.parseInt(yTextField.getText());
    			return y;
    		} catch(NumberFormatException ex) {
    			try {
    				AlertDisplaying.displayAlert("Unos za polje koordinate y nije odgovarajući.");
    				return null;
    			} catch(Exception e) {
    				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
    			}
    		}
    	}
    	AlertDisplaying.displayAlert("Niste unijeli y koordinatu.");
    	return null;
    }
    
    private Date checkDateFrom() {
    	if(!"".equals(fromTextField.getText())) {
    		try {
    			Date date = formatter.parse(fromTextField.getText());
    			return date;
    		} catch(ParseException ex) {
    			AlertDisplaying.displayAlert("Unos za polje datuma od nije odgovarajući.");
    			return null;
    		}
    	}
    	AlertDisplaying.displayAlert("Niste unijeli datum od.");
    	return null;
    }
    
    private Date checkDateTo() {
    	if(!"".equals(toTextField.getText())) {
    		try {
    			Date date = formatter.parse(toTextField.getText());
    			return date;
    		} catch(ParseException ex) {
    			AlertDisplaying.displayAlert("Unos za polje datuma do nije odgovarajući.");
    			return null;
    		}
    	}
    	AlertDisplaying.displayAlert("Niste unijeli datum do.");
    	return null;
    }
    
    private void showDetails(List<Integer> positions, String message) {
    	try {
    		new MapForm(positions, message).display();
    	} catch(Exception ex) {
    		logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
    	}
    }
    
    private boolean checkMessageTextField() {
    	if(!"".equals(messageTextField.getText())) {
    		return true;
    	}
    	AlertDisplaying.displayAlert("Niste unijeli poruku!");
    	return false;
    }
    
    private void sendFile(File file, boolean isZip) throws IOException {
    	byte[] buffer = new byte[(int) file.length()];
    	if(!isZip) {
	    	FileInputStream in = new FileInputStream(selectedFile);
			in.read(buffer, 0, buffer.length);
			in.close();
    	} else {
    		buffer = ZipUtil.getBytesOfZip(file);
    	}
			
		if(fileServerClient.sendFile(file.getName(), buffer, token, isZip)) {
			AlertDisplaying.displayAlert("Uspješno slanje fajla.");
				chatClient.setMessage("FILE#" + selectedFile.getName());
		} else {
			AlertDisplaying.displayAlert("Neuspješno slanje fajla.");
		}
    }
    
    private void saveUsage() {
    	if(!disabled) {
    		PersonService.saveUsage(person.getJmb(), dateFrom + "." + formatter.format(new Date()));
    	}
    }
    
    
}
