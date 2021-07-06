package model;

public enum SerializationType {
	JAVA(0),
	GSON(1),
	KRYO(2),
	XML(3);
	
	public final int value;
	
	private SerializationType(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
