package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import model.forms.ApplicationForm;

public class FileServerClient {
	private FileServerInterface fileServer;
	private Logger logger = ApplicationForm.logger;
	
	public ArrayList<byte[]> getFile(String fileName, String uuid) {
		ArrayList<byte[]> data = new ArrayList<>();
		try {
			data = fileServer.getFile(fileName, uuid);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return data;
	}
	
	public boolean sendFile(String fileName, byte[] buffer, String uuid) {
		boolean isOk = false;
		try {
			isOk = fileServer.sendFile(fileName, buffer, uuid, false);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return isOk;
	}
	
	public boolean start() {
		try {
			Properties properties = Configuration.readParameters();
			System.setProperty("java.security.policy", properties.getProperty("POLICY_FILE"));
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
		
			String name = "FileServer";
			int port = Integer.valueOf(properties.getProperty("FILE_PORT"));
			Registry registry = LocateRegistry.getRegistry(port);
			fileServer = (FileServerInterface) registry.lookup(name);
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			return false;
		}
	}
}
