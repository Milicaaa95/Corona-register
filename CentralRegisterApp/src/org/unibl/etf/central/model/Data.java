package org.unibl.etf.central.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.unibl.etf.central.config.Configuration;
import org.unibl.etf.central.util.RestUtil;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Data {
	private String infectedPersons;
	private String potentiallyInfectedPersons;
	private String notInfectedPersons;
	private String mapKey;
	private String blockedPersonsKey;
	
	private JedisPool instance = new JedisPool("localhost");
	private Gson gson = new Gson();
	
	public Data() {
		try {
			Properties properties = Configuration.readConfiguration();
			mapKey = properties.getProperty("MAP_KEY");
			notInfectedPersons = properties.getProperty("NOT_INFECTED_PERSONS");
			potentiallyInfectedPersons = properties.getProperty("POTENTIALLY_INFECTED_PERSONS");
			infectedPersons = properties.getProperty("INFECTED_PERSONS");
			blockedPersonsKey = properties.getProperty("BLOCKED_PERSONS");
		} catch (IOException e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public boolean isPersonBlocked(String token) {
		try(Jedis jedis = instance.getResource()) {
			List<String> blockedPersons = jedis.lrange(blockedPersonsKey, 0, -1);
			if(blockedPersons.contains(token)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean markPersonBlocked(String token) {
		try(Jedis jedis = instance.getResource()) {
			if(jedis.lpush(blockedPersonsKey, token) > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean markPersonInfected(MarkedPerson person) {
		try(Jedis jedis = instance.getResource()) {
			jedis.lrem(notInfectedPersons, 0, gson.toJson(person));
			jedis.lrem(potentiallyInfectedPersons, 0, gson.toJson(person));
			jedis.save();
			if(jedis.lpush(infectedPersons, gson.toJson(person)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public List<Integer> isPersonInfected(String uuid) {
		ArrayList<Integer> positions = new ArrayList<>();
		MarkedPerson person;
		try(Jedis jedis = instance.getResource()) {
			List<String> mapList = jedis.lrange(mapKey, 0, -1);
			Map map = gson.fromJson(mapList.get(0), Map.class);
			positions.add(map.getRows());
			positions.add(map.getColumns());
			List<String> infectedTokens = jedis.lrange(infectedPersons, 0, -1);
			for(String token : infectedTokens) {
				if(uuid.equals((person = gson.fromJson(token, MarkedPerson.class)).getUuid())) {
					positions.add(person.getX());
					positions.add(person.getY());
					break;
				}
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return positions;
	}
	
	public List<Integer> isPersonPotentiallyInfected(String uuid) {
		ArrayList<Integer> positions = new ArrayList<>();
		MarkedPerson person;
		try(Jedis jedis = instance.getResource()) {
			List<String> mapList = jedis.lrange(mapKey, 0, -1);
			Map map = gson.fromJson(mapList.get(0), Map.class);
			positions.add(map.getRows());
			positions.add(map.getColumns());
			List<String> potentiallyInfectedTokens = jedis.lrange(potentiallyInfectedPersons, 0, -1);
			for(String token : potentiallyInfectedTokens) {
				if(uuid.equals((person = gson.fromJson(token, MarkedPerson.class)).getUuid())) {
					positions.add(person.getX());
					positions.add(person.getY());
				}
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return positions;
	}
	
	public boolean markPersonPotentiallyInfected(MarkedPerson person) {
		try(Jedis jedis = instance.getResource()) {
			jedis.lrem(potentiallyInfectedPersons, 0, gson.toJson(person));
			jedis.lrem(notInfectedPersons, 0, gson.toJson(person));
			jedis.lrem(infectedPersons, 0, gson.toJson(person));
			jedis.save();
			if(jedis.lpush(potentiallyInfectedPersons, gson.toJson(person)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean markPersonNotInfected(MarkedPerson person) {
		try(Jedis jedis = instance.getResource()) {
			jedis.lrem(infectedPersons, 0, gson.toJson(person));
			jedis.lrem(potentiallyInfectedPersons, 0, gson.toJson(person));
			jedis.save();
			if(jedis.lpush(notInfectedPersons, gson.toJson(person)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean addMap(Map map) {
		try(Jedis jedis = instance.getResource()) {
			jedis.del(mapKey);
			jedis.save();
			if(jedis.lpush(mapKey, gson.toJson(map)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public String getMapInJson() {
		try(Jedis jedis = instance.getResource()) {
			List<String> map = jedis.lrange(mapKey, 0, -1);
			if(map.size() > 0) {
				return map.get(0);
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			return null;
		}
		return null;
	}
	
	public Map getMap() {
		try(Jedis jedis = instance.getResource()) {
			List<String> map = jedis.lrange(mapKey, 0, -1);
			if(map.size() > 0) {
				return gson.fromJson(map.get(0), Map.class);
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			return null;
		}
		return null;
	}
	
	public boolean updateMap(Map map) {
		try(Jedis jedis = instance.getResource()) {
			jedis.del(mapKey);
			jedis.save();
			if(jedis.lpush(mapKey, gson.toJson(map)) > 0) {
				jedis.save();
				return true;
			}
		} catch(Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public boolean addUsage(String jmb, String usage) {
		try(Jedis jedis = instance.getResource()) {
			if(jedis.lpush(jmb, usage) > 0) {
				jedis.save();
				return true;
			}
		} catch (Exception e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public List<String> getUsages(String jmb) {
		List<String> usages;
		try(Jedis jedis = instance.getResource()) {
			usages = jedis.lrange(jmb, 0, -1);
		}
		return usages;
	}
	
	public void closePool() {
		instance.close();
	}
}
