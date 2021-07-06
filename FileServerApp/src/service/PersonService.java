package service;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.service.TokenService;
import org.unibl.etf.service.TokenServiceServiceLocator;

import server.FileServer;

public class PersonService {
	private static Logger logger = FileServer.logger;
	private static TokenService service;
	
	static {
		try {
			service = new TokenServiceServiceLocator().getTokenService();
		} catch (ServiceException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public static boolean checkTokenValidity(String uuid) {
		boolean valid = false;		
		try {
			valid = service.checkTokenValidity(uuid);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		
		return valid;
	}
}
