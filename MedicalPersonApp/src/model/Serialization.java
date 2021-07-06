package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

import ajava.beans.XMLEncoder;
import configuration.Configuration;
import model.forms.ApplicationForm;

public class Serialization {
	private String path;
	private SimpleDateFormat formatter;
	private Logger logger = ApplicationForm.logger;
	
	public Serialization() {		
		try {
			path = Configuration.readParameters().getProperty("SERIALIZATION_PATH");
			formatter = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
			File folder = new File(path);
			if(!folder.exists()) {
				folder.mkdir();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	} 
	
	public void writeJava(String message) throws IOException {
		String fileName = formatter.format(new Date());
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + File.separator + fileName + ".out"));
		out.writeObject(message);
		out.flush();
		out.close();
	}
	
	public void writeGson(String message) throws IOException {
		Gson gson = new Gson();
		
		String fileName = formatter.format(new Date());
		PrintWriter out = new PrintWriter(new FileOutputStream(new File(path + File.separator + fileName + ".out")), true);
		String gsonMessage = gson.toJson(message);
		out.println(gsonMessage);
		out.flush();
		out.close();
	}
	
	public void writeKryo(String message) throws IOException {
		Kryo kryo = new Kryo();
		
		String fileName = formatter.format(new Date());
		Output out = new Output(new FileOutputStream(path + File.separator + fileName + ".out"));
		
		kryo.writeClassAndObject(out, message);
		out.flush();
		out.close();
	}
	
	public void writeXML(String message) throws IOException {
		String fileName = formatter.format(new Date());
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(path + File.separator + fileName + ".out"));
		encoder.writeObject(message);
		encoder.close();
	}
}
