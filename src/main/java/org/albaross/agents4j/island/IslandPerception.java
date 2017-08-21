package org.albaross.agents4j.island;

import static org.albaross.agents4j.island.IslandLocation.*;
import static org.albaross.agents4j.island.IslandWeather.*;

public class IslandPerception {

	public static final IslandPerception ORIGIN = new IslandPerception(16, AT_HQ, CLOUDS, CLOUDS, 4, true);

	private final int battery;
	private final IslandLocation location;
	private final IslandWeather weather;
	private final IslandWeather prediction;
	private final int change;
	private final boolean secured;

	public IslandPerception(int battery, IslandLocation location, IslandWeather weather, IslandWeather prediction, int change, boolean secured) {
		this.battery = battery;
		this.location = location;
		this.weather = weather;
		this.prediction = prediction;
		this.change = change;
		this.secured = secured;
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

	public int getChange() {
		return change;
	}

	public boolean isSecured() {
		return secured;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + battery;
		result = prime * result + change;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((prediction == null) ? 0 : prediction.hashCode());
		result = prime * result + (secured ? 1231 : 1237);
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
		if (change != other.change)
			return false;
		if (location != other.location)
			return false;
		if (prediction != other.prediction)
			return false;
		if (secured != other.secured)
			return false;
		if (weather != other.weather)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + battery + ", " + location + ", " + weather + " -> " + prediction + " in " + change + " ticks, "
				+ (secured ? "site secured" : "site not secured") + "]";
	}

}
