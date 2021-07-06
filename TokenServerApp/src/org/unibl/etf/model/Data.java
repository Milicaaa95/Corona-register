package org.unibl.etf.model;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.configuration.Configuration;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Data {
	private JedisPool instance = new JedisPool("localhost");
	private Gson gson = new Gson();
	
	private String tokensKey;
	private String usersKey;
	private Logger logger = Configuration.logger;
	
	public Data() {
		try {
			Properties properties = Configuration.readConfiguration();
			tokensKey = properties.getProperty("TOKENS_KEY");
			usersKey = properties.getProperty("USERS_KEY");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public String[] allTokens() {
		String[] tokens = null;
		try(Jedis jedis = instance.getResource()) {
			List<String> usersString = jedis.lrange(tokensKey, 0, -1);
			if(usersString.size() > 0) {
				tokens = new String[usersString.size()];
				for(int i = 0; i < usersString.size(); i++) {
					tokens[i] = gson.fromJson(usersString.get(i), String.class);
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return tokens;
	}
	
	public boolean addPerson(Person person) {
		try(Jedis jedis = instance.getResource()) {
			if(person.getUuid() != null) {
				jedis.lpush(tokensKey, gson.toJson(person.getUuid()));
				jedis.save();
			}
			if(jedis.lpush(usersKey, gson.toJson(person)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean removePerson(Person person) {
		try(Jedis jedis = instance.getResource()) {
			List<String> users = jedis.lrange(usersKey, 0, -1);
			for(String user : users) {
				Person person1 = gson.fromJson(user, Person.class);
				if(person1.getName().equals(person.getName()) && person1.getSurname().equals(person.getSurname()) && person1.getJmb().equals(person.getJmb())) {
					jedis.lrem(usersKey, 0, user);
					break;
				}
			}
			
			if(jedis.lrem(usersKey, 0, gson.toJson(person)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean removeToken(String uuid) {
		try(Jedis jedis = instance.getResource()) {
			if(jedis.lrem(tokensKey, 0, gson.toJson(uuid)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean isValidToken(String uuid) {
		try(Jedis jedis = instance.getResource()) {
			return jedis.lrange(tokensKey, 0, -1).contains(gson.toJson(uuid));
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public String getToken(Person person) {
		String token = null;
		try(Jedis jedis = instance.getResource()) {
			List<String> users = jedis.lrange(usersKey, 0, -1);
			for(String user : users) {
				Person person1 = gson.fromJson(user, Person.class);
				if(person1.getName().equals(person.getName()) && person1.getSurname().equals(person.getSurname()) && person1.getJmb().equals(person.getJmb())) {
					token = person1.getUuid();
					break;
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return token;
	}
	
	public String getPassword(Person person) {
		String password = null;
		try(Jedis jedis = instance.getResource()) {
			List<String> users = jedis.lrange(usersKey, 0, -1);
			for(String user : users) {
				Person person1 = gson.fromJson(user, Person.class);
				if(person1.getName().equals(person.getName()) && person1.getSurname().equals(person.getSurname()) && person1.getJmb().equals(person.getJmb())) {
					password = person1.getHashPassword();
					break;
				}
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return password;
	}
	
	public void closePool()  {
		instance.close();
	}
	
}
