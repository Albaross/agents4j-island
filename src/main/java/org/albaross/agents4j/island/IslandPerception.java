package org.albaross.agents4j.island;

import org.deeplearning4j.rl4j.space.Encodable;

public class IslandPerception implements Encodable {

	private final int site;
	private final boolean secured;
	private final int battery;
	private final IslandLocation location;
	private final IslandWeather weather;
	private final IslandWeather prediction;

	public IslandPerception(int site, boolean secured, int battery, IslandLocation location, IslandWeather weather, IslandWeather prediction) {
		this.site = site;
		this.secured = secured;
		this.battery = battery;
		this.location = location;
		this.weather = weather;
		this.prediction = prediction;
	}

	public int getSite() {
		return site;
	}

	public boolean isSecured() {
		return secured;
	}

	public int getBattery() {
		return battery;
	}

	public IslandLocation getLocation() {
		return location;
	}

	public IslandWeather getWeather() {
		return weather;
	}

	public IslandWeather getPrediction() {
		return prediction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + battery;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((prediction == null) ? 0 : prediction.hashCode());
		result = prime * result + (secured ? 1231 : 1237);
		result = prime * result + site;
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IslandPerception other = (IslandPerception) obj;
		if (battery != other.battery)
			return false;
		if (location != other.location)
			return false;
		if (prediction != other.prediction)
			return false;
		if (secured != other.secured)
			return false;
		if (site != other.site)
			return false;
		if (weather != other.weather)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IslandPerception [" + site + ", " + secured + ", " + battery + ", " + location + ", " + weather + ", " + prediction + "]";
	}

	@Override
	public double[] toArray() {
		return new double[] { (double) site / 16, secured ? 1 : 0, (double) battery / 4,
				(double) location.ordinal() / IslandLocation.values().length, (double) weather.ordinal() / IslandWeather.values().length,
				(double) prediction.ordinal() / IslandWeather.values().length, 1 };
	}

}
