package org.unibl.etf.central.model;

//oznaka osobe o zaraženosti na određenoj poziciji
public class MarkedPerson {
	private String uuid;
	private int x;
	private int y;
	
	public MarkedPerson() {
		super();
	}
	
	public MarkedPerson(String uuid, int x, int y) {
		super();
		this.uuid = uuid;
		this.x = x;
		this.y = y;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
}
