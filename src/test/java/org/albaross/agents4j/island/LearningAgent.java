package org.albaross.agents4j.island;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.albaross.agents4j.core.common.BasicAgent;
import org.albaross.agents4j.learning.EpsilonGreedyOperator;
import org.albaross.agents4j.learning.QTable;
import org.albaross.agents4j.learning.ValueFunction;

public class LearningAgent {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		BasicAgent<IslandPerception, IslandAction> learningAgent = BasicAgent.builder(IslandPerception.class, IslandAction.class)
				.add((EpsilonGreedyOperator<IslandPerception, IslandAction>) IslandAction::randomAction).add(new QTable<>()).getAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(learningAgent));

		long start, end, fullStart, fullEnd;
		fullStart = System.currentTimeMillis();
		for (int r = 0; r < 10000; r++) {
			System.out.println("Round " + r);
			start = System.currentTimeMillis();
			env.run();
			end = System.currentTimeMillis();
			System.out.println((end - start) / 1000.0 + " sek");
			System.out.println("rewards: " + env.getCumulative(0));
		}
		fullEnd = System.currentTimeMillis();
		((QTable<IslandPerception, IslandAction>) learningAgent.getComponent(ValueFunction.class)).save(new File("blub.json"));
		System.out.println("duration: " + (fullEnd - fullStart) / 1000.0 + " sek");
	}

}
