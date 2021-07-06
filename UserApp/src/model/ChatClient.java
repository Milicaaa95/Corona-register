package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import configuration.Configuration;
import controller.MainController;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.forms.ApplicationForm;

public class ChatClient {
	private Logger logger = ApplicationForm.logger;
	private BufferedReader in;
	private PrintWriter out;
	private SSLSocket socket;
	private String message;
	private VBox chatVBox;
	private VBox documentVBox;
	private String oldMessage = "";
	private boolean active = true;
	private TextField messageTextField;
	private Button sendButton;
	private String address;
	private int chat_port;
	
	public ChatClient(VBox chatVBox, TextField messageTextField, Button sendButton, VBox documentVBox) {		
		this.chatVBox = chatVBox;
		this.messageTextField = messageTextField;
		this.sendButton = sendButton;
		this.documentVBox = documentVBox;
		
		try {
			Properties properties = Configuration.readParameters();
			this.chat_port = Integer.valueOf(properties.getProperty("CHAT_PORT"));
			String keyStorePath = properties.getProperty("KEY_STORE_PATH");
			String keyStorePass = properties.getProperty("KEY_STORE_PASS");
			this.address = properties.getProperty("ADDRESS");
					
			System.setProperty("javax.net.ssl.trustStore", keyStorePath);
			System.setProperty("javax.net.ssl.trustStorePassword", keyStorePass);
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			this.socket = (SSLSocket) factory.createSocket(address, chat_port);		
			
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
			
			out.println("AUTH#PATIENT#" + MainController.token);
			String response = in.readLine();
				
			while(!"START".equals(response)) {
				out.println("AUTH#PATIENT#" + MainController.token);
				response = in.readLine();
				Thread.sleep(3000);
			}
			
			showFromMessage("Možete započeti chat!");
			disableOrEnable(false);
			
			
			Thread readThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String response;
					while(active) {
						try {
							response = in.readLine();
							if("FINISH".equals(response)) {
								active = false;
								showFromMessage("<< ZAVRŠENA KONVERZACIJA! >>");
								disableOrEnable(true);
							} else if(response != null) {
								showFromMessage(response);
							}
						} catch (IOException ex) {
							logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
						}
						
						}
					}
				});
				
				
			Thread writeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while(active) {
						if("END".equals(message)) {
							finish();
						} else if(message != null && checkIfMessageChanged()) {
							out.println("TOMEDICAL#" + message);
							showToMessage(message);
						}
						oldMessage = message;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ex) {
							logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
						}
					}
					finish();
					disableOrEnable(true);
					MainController.numberOfDocuments = 0;
				}
			});
			writeThread.setDaemon(true);
			readThread.setDaemon(true);
			writeThread.start();
			readThread.start();	
			
		} catch(Exception ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
	
	private void showFromMessage(String response) {
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
	
	private void showToMessage(String message) {
		if(!message.startsWith("FILE#")) {
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
	}
	
	public void finish() {
		out.println("END");
	}
	
	private boolean checkIfMessageChanged() {
		if(message.equals(oldMessage)) {
			return false;
		}
		return true;
	}
	
	private void clearChat() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatVBox.getChildren().clear();
			}
		});
	}
	
	private void disableOrEnable(boolean flag) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messageTextField.setDisable(flag);
				sendButton.setDisable(flag);
				documentVBox.getChildren().stream().forEach(e -> e.setDisable(flag));
			}
		});
	}
}
