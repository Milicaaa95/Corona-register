package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import configuration.Configuration;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.forms.ApplicationForm;
import model.forms.FileForm;
import server.FileServerClient;

public class ChatClient {
	private Logger logger = ApplicationForm.logger;
	private BufferedReader in;
	private PrintWriter out;
	private String message;
	private VBox chatVBox;
	private FileServerClient fileServerClient;
	private String oldMessage = "";
	private boolean active = true;
	private String patientToken;
	private Button sendMulticastButton;
	private Button sendPatientButton;
	private Button finishButton;
	
	public ChatClient(VBox chatVBox, FileServerClient fileServerClient, Button sendMulticastButton, Button sendPatientButton, Button finishButton) {
		this.chatVBox = chatVBox;
		this.fileServerClient = fileServerClient;
		this.sendMulticastButton = sendMulticastButton;
		this.sendPatientButton = sendPatientButton;
		this.finishButton = finishButton;
		
		try {
			Properties properties = Configuration.readParameters();
			int chat_port = Integer.valueOf(properties.getProperty("CHAT_PORT"));
			String keyStorePath = properties.getProperty("KEY_STORE_PATH");
			String keyStorePass = properties.getProperty("KEY_STORE_PASS");
			String address = properties.getProperty("ADDRESS");
					
			System.setProperty("javax.net.ssl.trustStore", keyStorePath);
			System.setProperty("javax.net.ssl.trustStorePassword", keyStorePass);
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(address, chat_port);
					
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			} catch(IOException ex) {
				logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void start() {
		try {
			clearChat();
			out.println("AUTH#MEDICAL");
			
			//tredovi koji osluškuju dolazne i odlazne poruke u skladu sa protokolom komunikacije na chat serveru
			Thread readThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String response;
					while(active) {
						try {
							response = in.readLine();
							if(response != null) {
								if("START MULTICAST".equals(response)) {
									enableMulticastButton();
								} else if("STOP MULTICAST".equals(response)) {
									disableMulticastButton();
								} else if(response.startsWith("TOKEN#")) {
									patientToken = response.substring(6);
									clearChat();
									showFromMessage("Možete započeti chat!");
									sendPatientButton.setDisable(false);
									finishButton.setDisable(false);
									disableMulticastButton();
								} else {
									showFromMessage(response);
								}
							}
							} catch (IOException e) {
								logger.log(Level.SEVERE, e.fillInStackTrace().toString());
							}
						}
					}
				});
		
				Thread writeThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while(active) {
							if("FINISH".equals(message) && checkIfMessageChanged()) {
								out.println("FINISH");
								clearChat();
								sendPatientButton.setDisable(true);
								finishButton.setDisable(true);
							} else if(message != null && message.startsWith("MULTICAST#") && checkIfMessageChanged()) {
								out.println(message);
								showToMessage("MEDICINAR:" + message.substring(10));
							} else if(message != null && checkIfMessageChanged()) {
								out.println("TOPATIENT#" + message);
								showToMessage(message);
							}
							oldMessage = message;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								logger.log(Level.SEVERE, e.fillInStackTrace().toString());
							}
						}
						finish();
						clearChat();
					}
				});
				readThread.setDaemon(true);
				writeThread.setDaemon(true);
				readThread.start();
				writeThread.start();
	       	} catch(Exception e) {	
	       		logger.log(Level.SEVERE, e.fillInStackTrace().toString());
	       	}
	}
	
	//prikaz poruka koje su poslane pacijentu
	private void showFromMessage(String response) throws IOException {
		if(response.startsWith("FILE#")) {
			ArrayList<byte[]> data = fileServerClient.getFile(response.substring(5), patientToken);
			showSavingFile(data);
		} else if(!"MULTICAST".equals(response)){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					HBox hbox = new HBox();
					hbox.setAlignment(Pos.CENTER_LEFT);
					hbox.getChildren().add(new Label(response));
					chatVBox.getChildren().add(hbox);					
				}
			});
		}
	}
	
	//prikaz poruka dobijenih od pacijenta
	private void showToMessage(String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				HBox hbox = new HBox();
				hbox.setAlignment(Pos.CENTER_RIGHT);
				hbox.getChildren().add(new Label(message));
				chatVBox.getChildren().add(hbox);
			}
		});
	}
	
	private boolean checkIfMessageChanged() {
		if(message.equals(oldMessage)) {
			return false;
		}
		return true;
	}
	
	private void enableMulticastButton() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				sendMulticastButton.setDisable(false);
			}
		});
	}
	
	private void disableMulticastButton() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				sendMulticastButton.setDisable(true);
			}
		});
	}
	
	public void finish() {
		out.println("END");
	}
	
	private void clearChat() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatVBox.getChildren().clear();
			}
		});
	}
	
	private void showSavingFile(ArrayList<byte[]> data) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					new FileForm("Pacijent vam je poslao fajl.", data, patientToken).display();
				} catch(Exception ex) {
					logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
				}
			}
		});
		
	}
	
	
}
