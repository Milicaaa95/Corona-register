package org.unibl.etf.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.unibl.etf.configuration.Configuration;
import org.unibl.etf.logs.ExceptionsLogger;

public class ChatServer {
	
	public static ArrayList<ChatServerThread> freeMedicals = new ArrayList<>();
	public static ArrayList<ChatServerThread> allActiveMedicals = new ArrayList<>();
	public static HashMap<ChatServerThread, ChatServerThread> chatMap = new HashMap<>();
	public static Logger logger;
	
	public static void main(String[] args) {		
		try {
			if(!Configuration.checkIfFileExists()) {
				Configuration.writeConfiguration();
				logger = new ExceptionsLogger().getLogger();
			} else {
				logger = new ExceptionsLogger().getLogger();
				
				Properties properties = Configuration.readParameters();
				int chat_port = Integer.valueOf(properties.getProperty("CHAT_PORT"));
				String keyStorePath = properties.getProperty("KEY_STORE_PATH");
				String keyStorePass = properties.getProperty("KEY_STORE_PASS");
				
				System.setProperty("javax.net.ssl.keyStore", keyStorePath);
				System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
				
				SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(chat_port);
				SSLSocket socket;
				
				MulticastServer multicastServer = new MulticastServer();
				
				Thread monitorMulticast = new Thread(new Runnable() {
					@Override
					public void run() {
						while(true) {
							if(allActiveMedicals.size() > 1) {
								allActiveMedicals.stream().forEach(e -> e.sendMessageToAllActiveMedicals("START MULTICAST"));
							} else {
								allActiveMedicals.stream().forEach(e -> e.sendMessageToAllActiveMedicals("STOP MULTICAST"));
							}
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				monitorMulticast.setDaemon(true);
				monitorMulticast.start();
				
				while(true) {
					socket = (SSLSocket) serverSocket.accept();
					new ChatServerThread(socket, multicastServer);
				}
			}
		} catch(IOException ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}	
}
