package model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppUsage {
	private Date logInTime;
	private Date logOutTime;
	private String sessionDuration;
	
	public AppUsage() {
		super();
	}

	public AppUsage(Date logInTime, Date logOutTime) {
		super();
		this.logInTime = logInTime;
		this.logOutTime = logOutTime;
		long difference = Math.abs(logOutTime.getTime() - logInTime.getTime());
		
		long seconds = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS);
		long minutes = TimeUnit.MINUTES.convert(seconds, TimeUnit.SECONDS);
		long hours = TimeUnit.HOURS.convert(minutes, TimeUnit.MINUTES);
		long days = TimeUnit.DAYS.convert(hours, TimeUnit.HOURS);
		
		this.sessionDuration = days + " : " + hours + " : " + minutes + " : " + seconds;		
	}

	public Date getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(Date logInTime) {
		this.logInTime = logInTime;
	}

	public Date getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(Date logOutTime) {
		this.logOutTime = logOutTime;
	}

	public String getSessionDuration() {
		return sessionDuration;
	}

	public void setSessionDuration(String sessionDuration) {
		this.sessionDuration = sessionDuration;
	}
	
	
}
