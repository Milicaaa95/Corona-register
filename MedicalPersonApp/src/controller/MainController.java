package controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.AlertDisplaying;
import model.ChatClient;
import model.MulticastClient;
import model.forms.ApplicationForm;
import model.forms.MapForm;
import server.FileServerClient;
import service.PersonService;

public class MainController {

    @FXML
    private VBox personsVBox;
    @FXML
    private VBox potentialContactsVBox;
    @FXML
    private ComboBox<String> markPersonComboBox;
    @FXML
    private ComboBox<String> positionsComboBox;
    @FXML
    private ListView<String> usersListView;
    @FXML
    private TextField messageTextField; 
    @FXML
    private VBox chatVBox;
    @FXML
    private Button sendButton;
    @FXML
    private Button sendMulticastButton;
    @FXML
    private Button finishButton;
    @FXML
    private TextField searchTextField;
    
    private String selectedMark = null;
    private String selectedToken = null;
    private String selectedPositions = null;
    private ChatClient chatClient;
    private FileServerClient fileServerClient;
    private MulticastClient multicastClient;
    private List<String> foundTokens;
    private Logger logger = ApplicationForm.logger;
    private List<String> tokens;
    
    @FXML
    public void initialize(Stage stage) {
    	messageTextField.setDisable(true);
    	sendButton.setDisable(true);
    	sendMulticastButton.setDisable(true);
    	
    	fileServerClient = new FileServerClient();

    	markPersonComboBox.getItems().addAll("Zaražena", "Potencijalno zaražena", "Nije zaražena");
    	
    	//like pretraga tokena, a zatim prikaz
    	searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
    		tokens = PersonService.getAllTokens();
    		usersListView.getItems().clear();
    		if(!"".equals(searchTextField.getText()) && tokens != null) {
				   foundTokens = tokens.stream().filter(e -> e.startsWith(searchTextField.getText())).collect(Collectors.toList());
				   usersListView.getItems().clear();
				   usersListView.getItems().addAll(foundTokens);
    			}
    		});
    	
    		usersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    		//prilikom označavanja osobe o zaraženosti, mora se odabrati pozicija na kojoj je bio
    		positionsComboBox.getItems().clear();
    		List<Integer> positions = PersonService.getPositionsForPerson(newValue);
    		for(int i = 2; i < positions.size(); i += 2) {
    			positionsComboBox.getItems().add(positions.get(i) + ", " + positions.get(i + 1));
    		}
    		
    		// osvježavanje potencijalnih kontakata izabrane osobe
    		potentialContactsVBox.getChildren().clear();
    		List<String> contacts = PersonService.getPotentialContacts(usersListView.getSelectionModel().getSelectedItem());
    		if(contacts != null) {
	    		for(String contact : contacts) {
	    			potentialContactsVBox.getChildren().add(new Label(contact));
	    		}
    		}
    	});

    	//signalizacija za prekid rada sa chat serverom
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(chatClient != null) { 
					chatClient.setActive(false);
					chatClient.finish();
					multicastClient.setActive(false);
				}
				
				try {
					Thread.sleep(500);
				} catch(InterruptedException ex) {
					logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
				}
				Platform.exit();
			}
		});
    }

    public void showMap() {
    	if(checkSelectedToken()) {
    		List<Integer> positions = PersonService.getPositionsForPerson(selectedToken);
    		try {
    			new MapForm(positions).display();
    		} catch(Exception ex) {
    			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
    		}
    	}
    }
    
    public void startChat() {
    	messageTextField.setDisable(false);
    	finishButton.setDisable(true);
    	chatClient = new ChatClient(chatVBox, fileServerClient, sendMulticastButton, sendButton, finishButton);
    	multicastClient = new MulticastClient(chatVBox);
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

    public void finishChat() {
    	chatClient.setMessage("FINISH");
    }
    
    public void sendMessageToPatient() {
    	if(checkMessageTextField()) {
    		chatClient.setMessage(messageTextField.getText());
    		messageTextField.clear();
    	}
    }
    
    public void sendMessageToMedicals() {
    	if(checkMessageTextField()) {
    		chatClient.setMessage("MULTICAST#" + messageTextField.getText());
    		messageTextField.clear();
    	}
    }
    
    //označavanje osobe o zaraženosti
    public void ok() {
    	if(checkSelectedToken() && checkSelectedMark() && checkSelectedPositions()) {
    		String content = "";
    		String[] positions = selectedPositions.split(", ");
    		int x = Integer.valueOf(positions[0]);
    		int y = Integer.valueOf(positions[1]);
    		
    		if("Zaražena".equals(selectedMark)) {
    			if(PersonService.markPersonInfected(selectedToken, x, y)) {
    				content = "Osoba uspješno označena!";
    			} else {
    				content = "Desila se greška prilikom označavanja osobe!";
    			}
    		} else if("Nije zaražena".equals(selectedMark)) {
    			if(PersonService.markPersonNotInfected(selectedToken, x, y)) {
    				content = "Osoba uspješno označena!";
    			} else {
    				content = "Desila se greška prilikom označavanja osobe!";
    			}
    		} else {
    			if(PersonService.markPersonPotentiallyInfected(selectedToken, x, y)) {
    				content = "Osoba uspješno označena!";
    			} else {
    				content = "Desila se greška prilikom označavanja osobe!";
    			}
    		}
    		AlertDisplaying.displayAlert(content);
    	}
    	usersListView.getSelectionModel().clearSelection();
    	markPersonComboBox.getSelectionModel().clearSelection();
    }
    
    public void blockPerson() {
    	if(checkSelectedToken()) {
    		if(PersonService.markPersonBlocked(selectedToken)) {
    			AlertDisplaying.displayAlert("Osoba uspješno blokirana.");
    		} else {
    			AlertDisplaying.displayAlert("Desila se greška prilikom blokiranja.");
    		}
    	}
    }
    
    private boolean checkMessageTextField() {
    	if(!"".equals((messageTextField.getText()))) {
    		return true;
    	}
    	AlertDisplaying.displayAlert("Niste unijeli poruku!");
    	return false;
    }
    
    private boolean checkSelectedToken() {
    	if((selectedToken = usersListView.getSelectionModel().getSelectedItem()) != null) {
    		return true;
    	}
    	AlertDisplaying.displayAlert("Nije izabran korisnik!");
    	return false;
    }
    
    private boolean checkSelectedMark() {
    	if((selectedMark = markPersonComboBox.getSelectionModel().getSelectedItem()) != null) {
    		return true;
    	}
    	AlertDisplaying.displayAlert("Nije označena osoba!");
    	return false;
    }
    
    private boolean checkSelectedPositions() {
    	if((selectedPositions = positionsComboBox.getSelectionModel().getSelectedItem()) != null) {
    		return true;
    	}
    	AlertDisplaying.displayAlert("Nisu izabrane pozicije!");
    	return false;
    }
}
