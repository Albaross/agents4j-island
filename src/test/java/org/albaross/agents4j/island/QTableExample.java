package org.albaross.agents4j.island;

import java.util.Arrays;

import org.albaross.agents4j.core.Agent;
import org.albaross.agents4j.core.BasicBuilder;
import org.albaross.agents4j.learning.components.QTableReplayComponent;
import org.albaross.agents4j.learning.operators.EpsilonGreedyOperator;

public class QTableExample {

	public static void main(String[] args) {
		BasicBuilder<IslandPerception, IslandAction> builder = new BasicBuilder<>();
		builder.add(new EpsilonGreedyOperator<>(IslandAction::randomAction, 0.1));
		builder.add(new QTableReplayComponent<>(1000000));
		Agent<IslandPerception, IslandAction> agent = builder.getAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(agent));

		int sum = 0;
		for (int r = 0; r < 2000000; r++) {
			env.run();
			sum = (int) env.getCumulative(0);
			System.out.println("Round " + r + ", Rewards " + sum + ", Ticks " + env.getCurrentTick());
		}
	}

}
