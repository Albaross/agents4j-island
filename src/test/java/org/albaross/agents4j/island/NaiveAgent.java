package org.albaross.agents4j.island;

import static org.albaross.agents4j.island.IslandAction.ASSEMBLE_PARTS;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_HQ;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_SITE;
import static org.albaross.agents4j.island.IslandLocation.AT_SITE;

import java.util.Arrays;

import org.albaross.agents4j.island.IslandAction;
import org.albaross.agents4j.island.IslandPerception;
import org.albaross.agents4j.learning.RLAgent;

public class NaiveAgent implements RLAgent<IslandPerception, IslandAction> {

	protected double sum = 0;

	@Override
	public void update(IslandPerception state, IslandAction action, double reward, IslandPerception next) {
		sum += reward;
		System.out.println(action + ": " + reward);
	}

	@Override
	public IslandAction generateAction(IslandPerception perception) {
		return !perception.isComplete() ? (perception.getLocation() != AT_SITE ? MOVE_TO_SITE : ASSEMBLE_PARTS) : MOVE_TO_HQ;
	}

	@Override
	public void notifySuccess() {
		System.out.println("sum of rewards: " + sum);
	}

	public static void main(String[] args) {
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(new NaiveAgent()));
		env.run();
	}

}
