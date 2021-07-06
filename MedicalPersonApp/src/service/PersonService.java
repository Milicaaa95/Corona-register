package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.forms.ApplicationForm;

public class PersonService {

	private static final String BASE_URL = "http://localhost:8080/CentralRegisterApp/api/users";
	private static Gson gson = new Gson();
	private static Logger logger = ApplicationForm.logger;
	
	public static List<String> getAllTokens() {
		List<String> tokens = new ArrayList<>();
		try {
			URL url = new URL(BASE_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			List<String> elements = null;
			while((line = in.readLine()) != null) {
				String[] parts = line.substring(1, line.length() - 1).split(",");
				elements = Arrays.asList(parts);
			}
			in.close();
			if(elements != null) {
				for(String element : elements) {
					tokens.add(gson.fromJson(element, String.class));
				}
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return tokens;
	}
	
	public static boolean markPersonBlocked(String uuid) {
		try {
			URL url = new URL(BASE_URL + "/blocked/" + uuid);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return true;
	}
	
	public static boolean markPersonInfected(String uuid, int x, int y) {
		try {
			URL url = new URL(BASE_URL + "/infected/" + uuid + "/" + x + "/" + y);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return true;
	}
	
	public static boolean markPersonNotInfected(String uuid, int x, int y) {
		try {
			URL url = new URL(BASE_URL + "/not-infected/" + uuid + "/" + x + "/" + y);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return true;
	}
	
	public static boolean markPersonPotentiallyInfected(String uuid, int x, int y) {
		try {
			URL url = new URL(BASE_URL + "/potentially-infected/" + uuid + "/" + x + "/" + y);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}
			connection.disconnect();			
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static List<Integer> getPositionsForPerson(String uuid) {
		List<Integer> positions = new ArrayList<>();
		try {
			URL url = new URL(BASE_URL + "/" + uuid + "/map");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return positions;
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			List<String> elements = null;
			while((line = in.readLine()) != null) {
				String[] parts = line.substring(1, line.length() - 1).split(",");
				elements = Arrays.asList(parts);
			}
			in.close();
			if(elements != null) {
				for(String element : elements) {
					positions.add(gson.fromJson(element, Integer.class));
				}
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return positions;
	}
	
	public static List<String> getPotentialContacts(String uuid) {
		List<String> contacts = new ArrayList<>();
		if(uuid != null) {
			try {
				URL url = new URL(BASE_URL + "/" + uuid + "/contacts");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");
				
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return null;
				}
				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String line;
				List<String> elements = null;
				while((line = in.readLine()) != null) {
					String[] parts = line.substring(1, line.length() - 1).split(",");
					elements = Arrays.asList(parts);
				}
				in.close();
				if(elements != null) {
					for(String element : elements) {
						contacts.add(gson.fromJson(element, String.class));
					}
				}
				connection.disconnect();
			} catch (MalformedURLException e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.fillInStackTrace().toString());
			}
		}
		return contacts;
	}	
}
