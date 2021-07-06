package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import logs.ExceptionsLogger;
import service.PersonService;

public class FileServer implements FileServerInterface {
	
	private static String path;
	private static Properties properties;
	public static Logger logger;
	
	public FileServer() throws RemoteException {

	}
	
	//dobijanje fajla u bajtovima
	public ArrayList<byte[]> getFile(String fileName, String uuid) throws RemoteException {
		ArrayList<byte[]> fileInfo = new ArrayList<byte[]>();
		boolean isZip = false;
		
		String filePath = path + File.separator + uuid + File.separator + fileName;
		File file = new File(filePath);
		byte[] data;
		try {
			if(file.length() <= 1024 * 1024) {
				data = new byte[(int) file.length()];
				FileInputStream in = new FileInputStream(file);
				in.read(data, 0, data.length);
				in.close();
			} else {
				File zipFile = ZipUtil.zip(file);
				data = ZipUtil.getBytesOfZip(zipFile);
				isZip = true;
				zipFile.delete();
			}
			
			fileInfo.add(data);
			fileInfo.add(fileName.getBytes());
			if(isZip) {
				fileInfo.add("zip".getBytes());
			}
		
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		
		return fileInfo;
	}
	
	//slanje fajla
	public boolean sendFile(String fileName, byte[] data, String uuid, boolean isZip) throws RemoteException {
		if(PersonService.checkTokenValidity(uuid)) {
			String folderPath = path + File.separator + uuid;
			
			File folder = new File(folderPath);
			if(!folder.exists()) {
				folder.mkdir();
			}
			
			try {
				ZipUtil.saveFile(folderPath, fileName, data, isZip);
				return true;
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
				return false;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		try {
			if(!Configuration.checkIfFileExists()) {
				Configuration.writeConfig();
			}
			logger = new ExceptionsLogger().getLogger();
			properties = Configuration.readParameters();
			path = properties.getProperty("PATH");
			File root = new File(path);
			if(!root.exists()) {
				root.mkdir();
			}
			
			monitorFolder();
			
			System.setProperty("java.security.policy", properties.getProperty("POLICY_FILE"));
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
		
			FileServer server = new FileServer();
			FileServerInterface stub = (FileServerInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(Integer.valueOf(properties.getProperty("FILE_PORT")));
			registry.rebind("FileServer", stub);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	// u slučaju da neki token postane nevalidan, iz foldera se brišu svi dokumenti vezani za taj token
	private static void monitorFolder() {
		Thread monitorThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					File filesFolder = new File(properties.getProperty("PATH"));
					for(File folder : filesFolder.listFiles()) {
						if(!PersonService.checkTokenValidity(folder.getName())) {
							for(File file : folder.listFiles()) {
								file.delete();
							}
							folder.delete();
						}
					}
					try {
						Thread.sleep(10000);
					} catch(InterruptedException e) {
						logger.log(Level.SEVERE, e.fillInStackTrace().toString());
					}
				}
			}
		});
		monitorThread.setDaemon(true);
		monitorThread.start();
	}
	
	
}
