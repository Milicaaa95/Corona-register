package org.unibl.etf.central.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.central.config.Configuration;
import org.unibl.etf.central.model.Data;
import org.unibl.etf.central.model.Map;
import org.unibl.etf.central.model.MarkedPerson;
import org.unibl.etf.central.model.PeriodLocation;
import org.unibl.etf.central.util.RestUtil;
@Path("/users")
public class UsersRest {
	
	private static HashMap<String, ArrayList<String>> potentialContactsMap = new HashMap<>();
	
	static {
		new Configuration();
		RestUtil.addMap();
		
		//osvježavanje potencijalnih kontakata za svakog pacijenta
		Thread refreshContactsThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Properties parameters = Configuration.readConfiguration();
					int seconds = Integer.valueOf(parameters.getProperty("seconds"));
					int distance = Integer.valueOf(parameters.getProperty("distance"));
					long intervalInMinutes = Long.valueOf(parameters.getProperty("interval"));
								
					ArrayList<String> contacts;
					List<String> allTokens;
					while(true) {
						allTokens = RestUtil.getAllTokens();
						if(allTokens != null) {
							for(String token : allTokens) {
								contacts = RestUtil.getPotentialContactsForUser(token, intervalInMinutes, distance);
								potentialContactsMap.put(token, contacts);
							}
						}
					
						Thread.sleep(seconds * 1000);
					}
	
				} catch (InterruptedException | IOException e) {
					RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
				}
			}
		});
		
		refreshContactsThread.setDaemon(true);
		refreshContactsThread.start();
		
	}
	
	public UsersRest() {
		
	}
	
	//svi validni tokeni
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> registeredUsers() {
		return RestUtil.getAllTokens();
	}
	
	//čuvanje upotrebe aplikacije za određeni uuid
	@Path("/usages/{jmb}/{usage}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveUsage(@PathParam("jmb") String jmb, @PathParam("usage") String usage) {
		Data data = new Data();
		if(data.addUsage(jmb, usage)) {
			data.closePool();
			return Response.status(200).build();
		}
		data.closePool();
		return Response.status(500).build();
	}
	
	//dobijanje svih upotreba aplikacije po uuid
	@Path("/usages/{jmb}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getUsages(@PathParam("jmb") String jmb) {
		List<String> usages;
		Data data = new Data();
		usages = data.getUsages(jmb);
		data.closePool();
		return usages;
	}
	
	//vraća pozicije na kojima je osoba zaražena
	@Path("/infected/{uuid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> isPersonInfected(@PathParam("uuid") String uuid) {
		List<Integer> positions = null;
		if(RestUtil.checkTokenValidity(uuid) && !RestUtil.isPersonBlocked(uuid)) {
			Data data = new Data();
			positions = data.isPersonInfected(uuid);
			data.closePool();
		}
		return positions;
	}
	
	//označavanje osobe o zaraženosti
	@Path("/infected/{uuid}/{x}/{y}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response markPersonInfected(@PathParam("uuid") String uuid, @PathParam("x") int x, @PathParam("y") int y) {
		Data data = new Data();
		if(data.markPersonInfected(new MarkedPerson(uuid, x, y))) {
			data.closePool();
			return Response.status(200).build();
		}
		data.closePool();
		return Response.status(500).build();
	}
	
	@Path("/potentially-infected/{uuid}/{x}/{y}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response markPersonPotentiallyInfected(@PathParam("uuid") String uuid, @PathParam("x") int x, @PathParam("y") int y) {
		Data data = new Data();
		if(data.markPersonPotentiallyInfected(new MarkedPerson(uuid, x, y))) {
			data.closePool();
			return Response.status(200).build();
		}
		data.closePool();
		return Response.status(500).build();
	}
	
	@Path("/potentially-infected/{uuid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> isPersonPotentiallyInfected(@PathParam("uuid") String uuid) {
		List<Integer> positions = null;
		if(RestUtil.checkTokenValidity(uuid) && !RestUtil.isPersonBlocked(uuid)) {
			Data data = new Data();
			positions = data.isPersonPotentiallyInfected(uuid);
			data.closePool();
		}
		return positions;
	}
	
	@Path("/not-infected/{uuid}/{x}/{y}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response markPersonNotInfected(@PathParam("uuid") String uuid, @PathParam("x") int x, @PathParam("y") int y) {
		Data data = new Data();
		if(data.markPersonNotInfected(new MarkedPerson(uuid, x, y))) {
			data.closePool();
			return Response.status(200).build();
		}
		data.closePool();
		return Response.status(500).build();
	}
	
	@Path("/{uuid}/map")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getMap(@PathParam("uuid") String uuid) {
		List<Integer> positions = new ArrayList<>();
		Data data = new Data();
		Map map = data.getMap();
		positions.add(map.getRows());
		positions.add(map.getColumns());
		positions.addAll(map.getPositionsForUser(uuid));
		return positions;
	}
	
	//dobijanje svih pozicija u posljednjih n dana
	@Path("/{uuid}/map/{days}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getMapForDays(@PathParam("uuid") String uuid, @PathParam("days") int days) {
		List<Integer> positions = new ArrayList<>();
		if(RestUtil.checkTokenValidity(uuid) && !RestUtil.isPersonBlocked(uuid)) {
			Data data = new Data();
			Map map = data.getMap();
			positions.add(map.getRows());
			positions.add(map.getColumns());
			positions.addAll(map.getPositionsForUserInPeriod(uuid, days));
			return positions;
		}
		return null;
	}
	
	//čuvanje lokacije osobe, tačna pozicija i vrijeme boravka	
	@Path("/location/{uuid}/{x}/{y}/{from}/{to}")	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveLocation(@PathParam("uuid") String uuid, @PathParam("x") int x, @PathParam("y") int y, 
			@PathParam("from") String from, @PathParam("to") String to) {
		if(RestUtil.checkTokenValidity(uuid) && !RestUtil.isPersonBlocked(uuid)) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
			Data data = new Data();
			Map map = data.getMap();
			if(map != null) {
				try {
					int positionInLocations = map.getPosition(x, y);
					Date fromDate = formatter.parse(from);
					Date toDate = formatter.parse(to);
					map.getLocations()[positionInLocations].add(new PeriodLocation(uuid, fromDate, toDate));
					if(data.updateMap(map)) {
						data.closePool();
						return Response.status(200).build();
					}
				} catch(ParseException e) {
					RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
				}
			}
			
			data.closePool();
		}
		return Response.status(500).build();
	}
	
	@Path("/blocked/{uuid}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response markPersonBlocked(@PathParam("uuid") String uuid) {
		Data data = new Data();
		if(data.markPersonBlocked(uuid)) {
			data.closePool();
			return Response.status(200).build();
		}
		data.closePool();
		return Response.status(500).build();
	}
	
	//dobijanje potencijalnih kontakata
	@Path("/{uuid}/contacts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPotentialContactsForUser(@PathParam("uuid") String uuid) {
		return potentialContactsMap.get(uuid);
	}	
}
