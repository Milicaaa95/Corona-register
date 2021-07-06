package org.unibl.etf.service;

import java.util.UUID;

import org.unibl.etf.configuration.Configuration;
import org.unibl.etf.model.Data;
import org.unibl.etf.model.Person;

public class TokenService {
	static {
		new Configuration();
	}
	
	public boolean checkTokenValidity(String uuid) {
		Data data = new Data();
		boolean ok = data.isValidToken(uuid);
		data.closePool();
		return ok;
	}
	
	public String getToken(Person person) {
		Data data = new Data();
		String token = data.getToken(person);
		if(token != null) {
			data.closePool();
			return token;
		} else {
			data.removePerson(person);
			token = UUID.randomUUID().toString();
			person.setUuid(token);
			if(data.addPerson(person)) {
				data.closePool();
				return token;
			}
		}
		data.closePool();
		return null;
	}
	
	public boolean updatePerson(Person person) {
		Data data = new Data();
		data.removePerson(person);
		data.removeToken(person.getUuid());
		if(data.addPerson(person)) {
			data.closePool();
			return true;
		}
		data.closePool();
		return false;
	}
	
	public String getPassword(Person person) {
		String password = null;
		Data data = new Data();
		password = data.getPassword(person);
		data.closePool();
		return password;
	}
	
	public boolean logOut(Person person) {
		Data data = new Data();
		boolean ok = false;		
		data.removePerson(person);
		data.removeToken(person.getUuid());
		
		person.setUuid(null);
		if(data.addPerson(person)) {
			ok = true;
		}
		
		data.closePool();
		return ok;
	}
	
	public String[] getTokens() {
		Data data = new Data();
		String[] tokens = data.allTokens();
		data.closePool();
		return tokens;
	}
}
