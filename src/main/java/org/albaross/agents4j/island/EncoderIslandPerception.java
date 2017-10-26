package org.albaross.agents4j.island;

import org.albaross.agents4j.learning.utils.StateEncoder;

public class EncoderIslandPerception implements StateEncoder<IslandPerception> {

	private static final int LOCATION_OFFSET = 3;
	private static final int WEATHER_OFFSET = LOCATION_OFFSET + IslandLocation.values().length;
	private static final int PREDICTION_OFFSET = WEATHER_OFFSET + +IslandWeather.values().length;

	private final int resolution;

	public EncoderIslandPerception(int resolution) {
		this.resolution = resolution;
	}

	@Override
	public double[] encode(IslandPerception state) {
		double[] img = new double[17];
		img[0] = (double) state.site / resolution;
		img[1] = (double) (state.secured ? 1 : 0);
		img[2] = (double) state.battery / resolution;
		img[LOCATION_OFFSET + state.location.ordinal()] = 1.0;
		img[WEATHER_OFFSET + state.weather.ordinal()] = 1.0;
		img[PREDICTION_OFFSET + state.prediction.ordinal()] = 1.0;
		return img;
	}

}
