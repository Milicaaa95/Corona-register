package org.unibl.etf.server.util;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.unibl.etf.server.ChatServer;
import org.unibl.etf.server.ChatServerThread;

public class FreeMedicalsReadWrite {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ReadLock readLock = lock.readLock();
	private WriteLock writeLock = lock.writeLock();
	private ArrayList<ChatServerThread> freeMedicals = ChatServer.freeMedicals;
	private ArrayList<ChatServerThread> activeMedicals = ChatServer.allActiveMedicals;
	
	public void addMedical(ChatServerThread socket) {
		writeLock.lock();
		freeMedicals.add(socket);
		writeLock.unlock();
	}
	
	public void addActiveMedical(ChatServerThread socket) {
		writeLock.lock();
		activeMedicals.add(socket);
		writeLock.unlock();
	}
	
	public ChatServerThread getMedical() {
		readLock.lock();
		ChatServerThread medical = null;
		if(freeMedicals.size() > 0) {
			medical = freeMedicals.get(0);
		}
		readLock.unlock();
		return medical;
	}
	
	public void removeMedical(ChatServerThread socket) {
		writeLock.lock();
		freeMedicals.remove(socket);
		writeLock.unlock();
	}
	
	public void removeActiveMedical(ChatServerThread socket) {
		writeLock.lock();
		activeMedicals.remove(socket);
		writeLock.unlock();
	}

	public ArrayList<ChatServerThread> getFreeMedicals() {
		readLock.lock();
		ArrayList<ChatServerThread> list = freeMedicals;
		readLock.unlock();
		return list;
	}	
	
}
