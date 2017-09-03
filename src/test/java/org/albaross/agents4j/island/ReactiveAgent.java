package org.albaross.agents4j.island;

import org.albaross.agents4j.core.Agent;
import org.albaross.agents4j.learning.RLAgent;

public class ReactiveAgent implements RLAgent<IslandPerception, IslandAction> {

	@Override
	public IslandAction generateAction(IslandPerception perception) {

		int bat = perception.getBattery();
		IslandLocation loc = perception.getLocation();
		IslandWeather wea = perception.getWeather();
		IslandWeather pre = perception.getPrediction();
		int rem = perception.getChange();
		boolean sec = perception.isSecured();
		boolean fin = perception.isComplete();

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(IslandPerception state, IslandAction action, double reward, IslandPerception next) {
		if (reward == -100)
			System.err.println(reward);
	}

}
