package org.albaross.agents4j.island;

public class IslandPerception {

	private final int battery;
	private final IslandLocation location;
	private final IslandWeather weather;
	private final IslandWeather prediction;
	private final boolean secured;
	private final boolean complete;

	public IslandPerception(int battery, IslandLocation location, IslandWeather weather, IslandWeather prediction, boolean secured,
			boolean complete) {
		this.battery = battery;
		this.location = location;
		this.weather = weather;
		this.prediction = prediction;
		this.secured = secured;
		this.complete = complete;
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

	public boolean isSecured() {
		return secured;
	}

	public boolean isComplete() {
		return complete;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + battery;
		result = prime * result + (complete ? 1231 : 1237);
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
		if (complete != other.complete)
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
		return "IslandPerception [battery=" + battery + ", location=" + location + ", weather=" + weather + ", prediction=" + prediction
				+ ", secured=" + secured + ", complete=" + complete + "]";
	}
	
}
