package org.unibl.etf.central.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.central.logs.ExceptionsLogger;
import org.unibl.etf.central.model.Data;
import org.unibl.etf.central.model.Map;
import org.unibl.etf.central.model.MarkedPerson;
import org.unibl.etf.central.model.PeriodLocation;
import org.unibl.etf.service.TokenService;
import org.unibl.etf.service.TokenServiceServiceLocator;

import org.joda.time.Interval;

public class RestUtil {
	private static TokenService service;
	public static Logger logger;
	
	static {
		logger = new ExceptionsLogger().getLogger();
		TokenServiceServiceLocator locator = new TokenServiceServiceLocator();
		try {
			service = locator.getTokenService();
		} catch (ServiceException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}
	
	public static List<String> getAllTokens() {
		String[] tokens = null;
		try {
			tokens = service.getTokens();
			if(tokens != null) {
				return Arrays.asList(tokens);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return null;
	}
	
	public static boolean checkTokenValidity(String uuid) {
		try {
			if(service.checkTokenValidity(uuid)) {
				return true;
			}
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public static boolean isPersonBlocked(String uuid) {
		boolean isBlocked;
		Data data = new Data();
		isBlocked = data.isPersonBlocked(uuid);
		data.closePool();
		
		return isBlocked;
	}
	
	private static boolean checkPotentialContact(PeriodLocation userLocation, PeriodLocation potentialContactLocation, long intervalInMinutes) {
		Interval userInterval = new Interval(userLocation.getFrom().getTime(), userLocation.getTo().getTime());
		Interval potentialContactInterval = new Interval(potentialContactLocation.getFrom().getTime(), potentialContactLocation.getTo().getTime());
		
		if(potentialContactInterval.overlaps(userInterval)) {
			Interval overlapInterval = potentialContactInterval.overlap(userInterval);
			long startInMillis = overlapInterval.getStartMillis();
			long endMillis = overlapInterval.getEndMillis();
			
			if(TimeUnit.MINUTES.convert(endMillis - startInMillis, TimeUnit.MILLISECONDS) >= intervalInMinutes) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getPotentialContactsForUser(String uuid, long intervalInMinutes, int distance) {
		ArrayList<String> contacts = new ArrayList<>();
		
		int counter = 1;
		Map map;
		ArrayList<Integer> userPositions;
				
		ArrayList<PeriodLocation> locations;
		int currentPosition;
		int row;
		int column;
		PeriodLocation locationOfUser;
				
		Data data = new Data();
		counter = 1;
		map = data.getMap();
		userPositions = map.getPositionsForUser(uuid);
		locationOfUser = null;
					
		for(int i = 0; i < userPositions.size(); i += 2) { 
			row = userPositions.get(i);
			column = userPositions.get(i + 1);
			locations = map.getFromPosition(map.getPosition(row, column));
			for(PeriodLocation location : locations) {
			if(uuid.equals(location.getUuid())) { 
				locationOfUser = location;
				locations.remove(location);
				break;
			} 
		}
			
		for(PeriodLocation location : locations) {
			if(data.isPersonPotentiallyInfected(uuid).size() > 2 || data.isPersonInfected(uuid).size() > 2 || data.isPersonInfected(location.getUuid()).size() > 2 || data.isPersonPotentiallyInfected(location.getUuid()).size() > 2) {
			if(RestUtil.checkPotentialContact(locationOfUser, location, intervalInMinutes)) {
				contacts.add(location.getUuid());
				contacts.add(row + "-" + column);
				}
			}
		}
						
		boolean isInfected = data.isPersonInfected(uuid).size() > 2;
		boolean isPotentiallyInfected = data.isPersonPotentiallyInfected(uuid).size() > 2;	
		
		//provjera po distanci sa svih strana na mapi od trenutne pozicije
		if(distance > 1) {
			while(counter < distance) {
			if(row - counter > 0) {
				currentPosition = map.getPosition(row - counter, column);
				locations = map.getFromPosition(currentPosition);
				for(PeriodLocation location : locations) {
					if(isInfected || isPotentiallyInfected || data.isPersonInfected(location.getUuid()).size() > 2 || data.isPersonPotentiallyInfected(location.getUuid()).size() > 2) {
						if(RestUtil.checkPotentialContact(locationOfUser, location, intervalInMinutes)) {
								contacts.add(location.getUuid());
								if(isInfected || isPotentiallyInfected) {
									if(data.isPersonInfected(location.getUuid()).size() == 2) {
										data.markPersonPotentiallyInfected(new MarkedPerson(location.getUuid(), row - counter, column));
									}
								} else {
									data.markPersonPotentiallyInfected(new MarkedPerson(uuid, row, column));
								}				
							}
						}
					}
				}
								
				if(row + counter < map.getRows()) {
					currentPosition = map.getPosition(row + counter, column);
					locations = map.getFromPosition(currentPosition);
					for(PeriodLocation location : locations) {
						if(isInfected || isPotentiallyInfected || data.isPersonInfected(location.getUuid()).size() > 2 || data.isPersonPotentiallyInfected(location.getUuid()).size() > 2) {
							if(RestUtil.checkPotentialContact(locationOfUser, location, intervalInMinutes)) {
									contacts.add(location.getUuid());
									if(isInfected || isPotentiallyInfected) {
										if(data.isPersonInfected(location.getUuid()).size() == 2) {
											data.markPersonPotentiallyInfected(new MarkedPerson(location.getUuid(), row +counter, column));
										}
									} else {
										data.markPersonPotentiallyInfected(new MarkedPerson(uuid, row, column));
									}
								}
							}
						}
					}
								
					if(column - counter > 0) {
						currentPosition = map.getPosition(row , column - counter);
						locations = map.getFromPosition(currentPosition);
							for(PeriodLocation location : locations) {
								if(isInfected || isPotentiallyInfected || data.isPersonInfected(location.getUuid()).size() > 2 || data.isPersonPotentiallyInfected(location.getUuid()).size() > 2) {
									if(RestUtil.checkPotentialContact(locationOfUser, location, intervalInMinutes)) {
										contacts.add(location.getUuid());
										if(isInfected || isPotentiallyInfected) {
											if(data.isPersonInfected(location.getUuid()).size() == 2) {
												data.markPersonPotentiallyInfected(new MarkedPerson(location.getUuid(), row, column - counter));
											}
										} else {
											data.markPersonPotentiallyInfected(new MarkedPerson(uuid, row, column));
										}
									}
								}
							}
						}
								
						if(column + counter < map.getColumns()) {
							currentPosition = map.getPosition(row , column + counter);
							locations = map.getFromPosition(currentPosition);
							for(PeriodLocation location : locations) {
								if(isInfected || isPotentiallyInfected || data.isPersonInfected(location.getUuid()).size() > 2 || data.isPersonPotentiallyInfected(location.getUuid()).size() > 2) {
									if(RestUtil.checkPotentialContact(locationOfUser, location, intervalInMinutes)) {
										contacts.add(location.getUuid());
										if(isInfected || isPotentiallyInfected) {
											if(data.isPersonInfected(location.getUuid()).size() == 2) {
												data.markPersonPotentiallyInfected(new MarkedPerson(location.getUuid(), row, column + counter));
											}
										} else {
											data.markPersonPotentiallyInfected(new MarkedPerson(uuid, row, column));
										}
									}
								}
							}
						}
						counter++;
					}
				}
			}
					
			data.closePool();
		
		return contacts;
	}
	
	public static void addMap() {
		Data data = new Data();
		if(data.getMap() == null) {
			data.addMap(new Map());
		}
		data.closePool();
	}
}
