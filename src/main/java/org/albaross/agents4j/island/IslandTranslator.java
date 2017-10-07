package org.albaross.agents4j.island;

import org.albaross.agents4j.extraction.Premise;
import org.albaross.agents4j.extraction.Translator;

/**
 * 
 * @author Manuel Barbi
 *
 */
public class IslandTranslator implements Translator<IslandPerception> {

	@Override
	public Premise stateToPremise(IslandPerception state) {
		Premise premise = new Premise();

		premise.put("site", state.getSite());
		premise.put("secured", state.isSecured());
		premise.put("battery", state.getBattery());
		premise.put("location", state.getLocation());
		premise.put("weather", state.getWeather());
		premise.put("prediction", state.getPrediction());

		return premise;
	}

}
