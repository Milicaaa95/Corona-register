package model;

import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.forms.ApplicationForm;

public class HashUtil {
	private static Logger logger = ApplicationForm.logger;
	
	public static String getSHA256(String password) {
        String passwordHash = "";
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();
            passwordHash = IntStream.range(0, byteData.length)
                                    .mapToObj(i -> Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1))
                                    .collect(Collectors.joining());
        } catch(Exception ex) {
        	logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
        }
        return passwordHash;
    }
}
