package org.unibl.etf.central.model;

import java.util.Date;

//predstavlja objekte koji daju informacije o tacnom periodu boravka neke osobe, koji se čuvaju u nizu pozicija mape
public class PeriodLocation {
	private String uuid;
	private Date from;
	private Date to;	
	
	public PeriodLocation() {
		super();
	}
	
	public PeriodLocation(String uuid, Date from, Date to) {
		super();
		this.uuid = uuid;
		this.from = from;
		this.to = to;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	
	public Date getTo() {
		return to;
	}
	
	public void setTo(Date to) {
		this.to = to;
	}
	
	public long getDifference() {
		return Math.abs(to.getTime() - from.getTime());
	}
}
