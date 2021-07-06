package org.unibl.etf.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.server.util.ChatMapReadWrite;
import org.unibl.etf.server.util.FreeMedicalsReadWrite;

public class ChatServerThread extends Thread {
	private SSLSocket socket;
	private MulticastServer multicastServer;
	private BufferedReader in;
	private PrintWriter out;
	private String token;
	private FreeMedicalsReadWrite freeMedicalsReadWrite;
	private ChatMapReadWrite chatMapReadWrite;
	private Logger logger = ChatServer.logger;
	
	public ChatServerThread(SSLSocket socket, MulticastServer multicastServer) {
		this.socket = socket;
		this.multicastServer = multicastServer;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		} catch(IOException ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
		start();
	}
	
	@Override
	public void run() {
		try {
			String request = "";
			freeMedicalsReadWrite = new FreeMedicalsReadWrite();
			chatMapReadWrite = new ChatMapReadWrite();
			ChatServerThread patientForMedical = null;
			ChatServerThread medicalForPatient = null;
			ChatServerThread medicalPerson = null;
	
			while(!"END".equals(request = in.readLine())) {
				//prijava medicinara
				if("AUTH#MEDICAL".equals(request)) {
					freeMedicalsReadWrite.addMedical(this);
					freeMedicalsReadWrite.addActiveMedical(this);
					//prijava pacijenta
				} else if(request.startsWith("AUTH#PATIENT#")) {
					medicalPerson = freeMedicalsReadWrite.getMedical();
					if(medicalPerson != null) {
						freeMedicalsReadWrite.removeMedical(medicalPerson);
						chatMapReadWrite.add(this, medicalPerson);
						sendMessage("START", out);
						token = request.substring(13);
						sendMessage("TOKEN#" + token, medicalPerson.out);
					} else {
						sendMessage("AUTH NOT OK", out);
					}
				} else if(request.startsWith("TOMEDICAL#")) {
					//slanje poruke medicinskoj osobi
					medicalForPatient = chatMapReadWrite.getMedicalForPatient(this);
					if(medicalForPatient != null) {
						sendMessage(request.substring(10), medicalForPatient.out);
					}
				} else if(request.startsWith("TOPATIENT#")) {
					//slanje poruke pacijentu
					patientForMedical = chatMapReadWrite.getPatientForMedical(this);
					if(patientForMedical != null) {
						sendMessage(request.substring(10), patientForMedical.out);
					}
				} else if("FINISH".equals(request)) {
					// kada medicinar zavrsi chat sa pacijentom
					patientForMedical = chatMapReadWrite.getPatientForMedical(this);
					if(patientForMedical != null) {
						sendMessage("FINISH", patientForMedical.out);
						chatMapReadWrite.removeFromChatMap(patientForMedical, this);
						freeMedicalsReadWrite.addMedical(this);
					}
				} else if(request.startsWith("FILE#TOMEDICAL#")) {
					//slanje fajla medicinaru
					medicalForPatient = chatMapReadWrite.getMedicalForPatient(this);
					if(medicalForPatient != null) {
						sendMessage("FILE#" + request.substring(15), medicalForPatient.out);
					}
				} else if(request.startsWith("MULTICAST#")) {
					//slanje multicast poruke
					multicastServer.setMessage(request.substring(10));
					sendMessage("MULTICAST", out);
				}
			}
			
			//kada osoba više nije aktivna, briše se iz liste aktivnih
			if(medicalPerson != null) {
				chatMapReadWrite.removeFromChatMap(this, medicalForPatient);
				if(medicalForPatient != null) { 
					medicalForPatient.out.println("<< PACIJENT SE ODJAVIO! >>");
				}
			} else {
				chatMapReadWrite.removeFromChatMap(patientForMedical, this);
				freeMedicalsReadWrite.removeMedical(this);
				freeMedicalsReadWrite.removeActiveMedical(this);
				if(patientForMedical != null) {
					patientForMedical.out.println("<< MEDICINAR SE ODJAVIO! >>");
				}
			}
			
			multicastServer.setActive(false);
			in.close();
			out.close();
			socket.close();
		} catch(IOException ex) {
			logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
		}
	}
	
	private void sendMessage(String message, PrintWriter out) {
		out.println(message);
	}
	
	public void sendMessageToAllActiveMedicals(String message) {
		for(ChatServerThread medical : freeMedicalsReadWrite.getFreeMedicals()) {
			sendMessage(message, medical.out);
		}
	}
	
	
	
}
