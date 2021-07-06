package org.unibl.etf.central.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.unibl.etf.central.config.Configuration;
import org.unibl.etf.central.util.RestUtil;

public class Map {
	private int rows;
	private int columns;
	ArrayList<PeriodLocation>[] locations;
	
	public Map() {
		ArrayList<Integer> dimensions;
		try {
			dimensions = new Configuration().getMapDimensions();
			rows = dimensions.get(0);
			columns = dimensions.get(1);
			locations = new ArrayList[rows * columns];
			for(int number = 0; number < rows * columns; number++) {
					locations[number] = new ArrayList<>();
			}
		} catch (IOException e) {
			RestUtil.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	
	public ArrayList<PeriodLocation>[] getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<PeriodLocation>[] locations) {
		this.locations = locations;
	}

	public ArrayList<PeriodLocation> getFromPosition(int position) {
		return locations[position];
	}
	
	public int getRow(int position) {
		return position / columns;
	}
	
	public int getColumn(int position) {
		return position % columns;
	}
	
	public int getPosition(int row, int column) {
		return row * columns + column;
	}
	
	public ArrayList<Integer> getPositionsForUser(String uuid) {
		ArrayList<Integer> positions = new ArrayList<>();
		for(int position = 0; position < rows * columns; position++) {
			for(PeriodLocation location : locations[position]) {
				if(uuid.equals(location.getUuid())) {
					positions.add(getRow(position));
					positions.add(getColumn(position));
				}
			}
		}
		return positions;
	}
	
	public ArrayList<Integer> getPositionsForUserInPeriod(String uuid, int days) {
		ArrayList<Integer> positions = new ArrayList<>();
		long daysInMillis = TimeUnit.DAYS.toMillis(days);
		for(int position = 0; position < rows * columns; position++) {
			for(PeriodLocation location : locations[position]) {
				if(uuid.equals(location.getUuid()) && location.getDifference() <= daysInMillis) {
					positions.add(getRow(position));
					positions.add(getColumn(position));
				}
			}
		}
		return positions;
	}
		
}
