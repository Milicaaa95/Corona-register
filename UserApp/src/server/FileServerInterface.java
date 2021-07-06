package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface FileServerInterface extends Remote {
	
	public byte[] getFile(String fileName, String uuid) throws RemoteException;
	
	public boolean sendFile(String fileName, byte[] data, String uuid, boolean isZip) throws RemoteException;

}
