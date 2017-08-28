package org.albaross.agents4j.island;

import java.util.Arrays;

import org.albaross.agents4j.core.common.BasicAgent;
import org.albaross.agents4j.learning.EpsilonGreedyOperator;
import org.albaross.agents4j.learning.QTable;
import org.albaross.agents4j.learning.RLWrapper;

public class LearningAgent extends RLWrapper<IslandPerception, IslandAction> {

	protected double sum = 0;

	@Override
	public void update(IslandPerception state, IslandAction action, double reward, IslandPerception next) {
		super.update(state, action, reward, next);
		sum += reward;
	}

	@Override
	public void notifySuccess() {
		System.out.println("sum of rewards: " + sum);
	}

	public LearningAgent() {
		super(BasicAgent.builder(IslandPerception.class, IslandAction.class)
				.add((EpsilonGreedyOperator<IslandPerception, IslandAction>) IslandAction::randomAction).add(new QTable<>()).getAgent());
	}

	public static void main(String[] args) {
		LearningAgent learningAgent = new LearningAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(learningAgent));

		long start, end;
		for (int r = 0; r < 100; r++) {
			start = System.currentTimeMillis();
			learningAgent.sum = 0;
			env.run();
			end = System.currentTimeMillis();
			System.out.println(env.getCurrentTick());
			System.out.println((end - start) / 1000.0 + " sek");
		}
	}

}
