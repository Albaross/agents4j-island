package org.albaross.agents4j.island;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.albaross.agents4j.extraction.AprioriMemExtraction;
import org.albaross.agents4j.extraction.StateActionPair;
import org.albaross.agents4j.learning.ReplayAgent;

public class LearningAgent {

	public static void main(String[] args) throws IOException {
		ReplayAgent<IslandPerception, IslandAction> agent = new ReplayAgent<>(IslandAction::randomAction, 100000, 2000);
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(agent));

		int rounds = 2000000;

		for (int r = 0; r < rounds; r++) {
			env.run();
			System.out.println("Round " + r + ", Rewards " + (int) env.getCumulative(0) + ", Ticks " + env.getCurrentTick());
		}

		List<StateActionPair<IslandPerception, IslandAction>> seq = new LinkedList<>();
		agent.getLastSequence().forEach((e) -> {
			seq.add(new StateActionPair<IslandPerception, IslandAction>(e.getState(), e.getAction()));
		});

		AprioriMemExtraction<IslandPerception, IslandAction> ext = new AprioriMemExtraction<>();
		System.out.println(ext.extract(seq));
	}

}
