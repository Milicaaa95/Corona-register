package org.unibl.etf.server.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.unibl.etf.server.ChatServer;
import org.unibl.etf.server.ChatServerThread;

public class ChatMapReadWrite {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ReadLock readLock = lock.readLock();
	private WriteLock writeLock = lock.writeLock();
	private HashMap<ChatServerThread, ChatServerThread> chatMap = ChatServer.chatMap;
	
	public void add(ChatServerThread patient, ChatServerThread medical) {
		writeLock.lock();
		chatMap.put(patient, medical);
		writeLock.unlock();
	}
	
	public ChatServerThread getMedicalForPatient(ChatServerThread patient) {
		return chatMap.get(patient);
	}
	
	public ChatServerThread getPatientForMedical(ChatServerThread medical) {
		ChatServerThread patient = null;
		readLock.lock();
		for(Entry<ChatServerThread, ChatServerThread> entry : chatMap.entrySet()) {
			if(entry.getValue().equals(medical)) {
				patient = entry.getKey();
				break;
			}
		}
		readLock.unlock();
		return patient;
	}
	
	public void removeFromChatMap(ChatServerThread patient, ChatServerThread medical) {
		writeLock.lock();
		chatMap.remove(patient, medical);
		writeLock.unlock();
	}

	public HashMap<ChatServerThread, ChatServerThread> getChatMap() {
		readLock.lock();
		HashMap<ChatServerThread, ChatServerThread> map = chatMap;
		readLock.unlock();
		return map;
	}
}
