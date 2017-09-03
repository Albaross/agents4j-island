package org.albaross.agents4j.island;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.albaross.agents4j.core.common.BasicAgent;
import org.albaross.agents4j.learning.EpsilonGreedyOperator;
import org.albaross.agents4j.learning.QTable;
import org.albaross.agents4j.learning.RLWrapper;
import org.albaross.agents4j.learning.ValueFunction;

public class LearningAgent {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		BasicAgent<IslandPerception, IslandAction> learningAgent = BasicAgent.builder(IslandPerception.class, IslandAction.class)
				.add((EpsilonGreedyOperator<IslandPerception, IslandAction>) IslandAction::randomAction).add(new QTable<>()).getAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(new RLWrapper<>(learningAgent)));

		int r = 0;
		while (true) {
			System.out.println("Round " + r);
			env.run();
			System.out.println("rewards: " + env.getCumulative(0));

			if (r % 5000 == 100)
				((QTable<IslandPerception, IslandAction>) learningAgent.getComponent(ValueFunction.class))
						.save(new File("qtables/qtable" + r + ".json"));

			r++;
		}
	}

}
