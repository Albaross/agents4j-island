package org.albaross.agents4j.island;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.albaross.agents4j.learning.QTable;
import org.albaross.agents4j.learning.RLAgent;

public class LearningAgent2 {

	public static void main(String[] args) throws IOException {
		RLRevAgent learningAgent = new RLRevAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(learningAgent));

		int r = 0;
		while (true) {
			learningAgent.learn();

			System.out.println("Round " + r);
			env.run();
			System.out.println("rewards: " + env.getCumulative(0));

			if (env.getCumulative(0) >= -2000 || r % 500 == 300)
				learningAgent.table.save(new File("qtables/qtable_" + r + "_" + env.getCumulative(0) + ".json"));

			r++;
		}
	}

	private static class RLRevAgent implements RLAgent<IslandPerception, IslandAction> {

		private QTable<IslandPerception, IslandAction> table = new QTable<>();
		private final Random RND = new Random();
		private List<Experience> lesson = new LinkedList<>();

		@Override
		public IslandAction generateAction(IslandPerception perception) {
			if (RND.nextDouble() <= 0.25)
				return IslandAction.randomAction();

			IslandAction action = table.getBestAction(perception);

			return action != null ? action : IslandAction.randomAction();
		}

		@Override
		public void update(IslandPerception state, IslandAction action, double reward, IslandPerception next) {
			lesson.add(0, new Experience(state, action, reward, next));
			if (lesson.size() > 100000)
				learn();
		}

		public void learn() {
			for (Experience exp : lesson)
				table.update(exp.state, exp.action, exp.reward, exp.next);

			lesson = new LinkedList<>();
		}

	}

	private static class Experience {

		private final IslandPerception state;
		private final IslandAction action;
		private final double reward;
		private final IslandPerception next;

		public Experience(IslandPerception state, IslandAction action, double reward, IslandPerception next) {
			this.state = state;
			this.action = action;
			this.reward = reward;
			this.next = next;
		}
	}

}
