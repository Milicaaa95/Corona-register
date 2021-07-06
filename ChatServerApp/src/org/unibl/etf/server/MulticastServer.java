package org.unibl.etf.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.configuration.Configuration;

public class MulticastServer extends Thread {
	private MulticastSocket socket;
	private int port;
	private InetAddress address;
	private String message;
	private String oldMessage = "";
	private boolean active = true;
	private Logger logger = ChatServer.logger;
	
	public MulticastServer() {
		try {
			Properties properties = Configuration.readParameters();
			port = Integer.valueOf(properties.getProperty("CHAT_PORT"));
			String host = properties.getProperty("MULTICAST_ADDRESS");
			
			socket = new MulticastSocket();
			address = InetAddress.getByName(host);
			socket.joinGroup(address);

			start();
		} catch(IOException ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		while(active) {
			if(message != null && checkIfMessageChanged()) {
				oldMessage = message;
				sendMessage();
			}
			try {
				Thread.sleep(1000);
			} catch(InterruptedException ex) {
				logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
			}
		}
		socket.close();
	}
	
	private void sendMessage() {
		byte[] buffer = new byte[1024];
		buffer = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
		try {
			socket.send(packet);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
	
	private boolean checkIfMessageChanged() {
		if(message.equals(oldMessage)) {
			return false;
		}
		return true;
	}
}
