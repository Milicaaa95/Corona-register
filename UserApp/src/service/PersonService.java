package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.AppUsage;
import model.forms.ApplicationForm;

public class PersonService {

	private static final String BASE_URL = "http://localhost:8080/CentralRegisterApp/api/users";
	private static Gson gson = new Gson();
	private static Logger logger = ApplicationForm.logger;
	
	public static boolean saveUsage(String jmb, String usage) {
		try {
			URL url = new URL(BASE_URL + "/usages/" + jmb + "/" + usage);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			connection.disconnect();
			return true;
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public static List<AppUsage> getUsages(String jmb) {
		List<AppUsage> usages = null;
		try {
			URL url = new URL(BASE_URL + "/usages/" + jmb);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			List<String> elements = null;
			String[] parts;
			SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
			Date dateFrom;
			Date dateTo;
			while((line = in.readLine()) != null) {
				parts = line.substring(1, line.length() - 1).split(",");
				elements = Arrays.asList(parts);
			}
			in.close();
			
			if(elements != null && elements.size() > 0) {
				usages = new ArrayList<>();
				for(String element : elements) {
					if(!"".equals(element)) {
						element = element.substring(1, element.length() - 1);
						parts = element.split("\\.");
						dateFrom = formater.parse(parts[0]);
						dateTo = formater.parse(parts[1]);
						usages.add(new AppUsage(dateFrom, dateTo));
					}
				}
			}
			
			connection.disconnect();
			return usages;
			
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return usages;
	}
	
	public static boolean saveLocation(String uuid, int x, int y, String from, String to) {
		try {
			URL url = new URL(BASE_URL + "/location/" + uuid + "/" + x + "/" + y + "/" + from + "/" + to);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}
			
			connection.disconnect();
			return true;
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return false;
	}
	
	public static List<Integer> isPersonInfected(String uuid) {
		List<Integer> positions = new ArrayList<>();
		try {
			URL url = new URL(BASE_URL + "/infected/" + uuid);
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
					positions.add(gson.fromJson(element, Integer.class));
				}
			}
			
			connection.disconnect();
			return positions;
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (ProtocolException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return null;
	}
	
	public static List<Integer> isPersonPotentiallyInfected(String uuid) {
		List<Integer> positions = new ArrayList<>();
		try {
			URL url = new URL(BASE_URL + "/potentially-infected/" + uuid);
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
					positions.add(gson.fromJson(element, Integer.class));
				}
			}
			
			connection.disconnect();
			return positions;
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (ProtocolException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return null;
	}
	
	public static List<Integer> getPositionsForUser(String uuid, int numberOfDays) {
		List<Integer> positions = new ArrayList<>();
		try {
			URL url = new URL(BASE_URL + "/" + uuid + "/map/" + numberOfDays);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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
					positions.add(gson.fromJson(element, Integer.class));
				}
			}
			
			connection.disconnect();
			return positions;
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
		return null;
	}
	
}
