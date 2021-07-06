package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.forms.ApplicationForm;

public class MulticastClient extends Thread {
	private Logger logger = ApplicationForm.logger;
	private boolean active = true;
	private MulticastSocket socket;
	private VBox chatVBox;
	private Serialization serialization;
	private int counter = -1;
	
	public MulticastClient(VBox chatVBox) {
		this.chatVBox = chatVBox;
		serialization = new Serialization();
		try {
			Properties properties = Configuration.readParameters();
			int multicast_port = Integer.valueOf(properties.getProperty("CHAT_PORT"));
			String multicast_address = properties.getProperty("MULTICAST_ADDRESS");
			socket = new MulticastSocket(multicast_port);
			InetAddress address = InetAddress.getByName(multicast_address);
			socket.joinGroup(address);
			
			setDaemon(true);
			start();			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public void run() {
		String message;
		while(active) {
			try {
				message = readMessage();
				counter++;
				serialize(counter, message);
				showMessage(message);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			}
		}
		socket.close();
	}
	
	public String readMessage() throws IOException {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		String message = new String(packet.getData(), 0, packet.getLength());
		return message;
	}
	
	//prikaz multicast poruke
	public void showMessage(String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				HBox hBox = new HBox();
				hBox.setAlignment(Pos.CENTER_LEFT);
				Label label = new Label("MEDICINAR:" + message);
				hBox.getChildren().add(label);
				chatVBox.getChildren().add(hBox);
			}
		});
	}
	
	private void serialize(int count, String message) {
		try {
			if(counter % 4 == SerializationType.JAVA.value) {
				serialization.writeJava(message);
			} else if(counter % 4 == SerializationType.GSON.value)  {
				serialization.writeGson(message);
			} else if(counter % 4 == SerializationType.KRYO.value)  {
				serialization.writeKryo(message);
			} else {
				serialization.writeXML(message);
				counter = -1;
			}
		} catch(IOException e)  {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
}
