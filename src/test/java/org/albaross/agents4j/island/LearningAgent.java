package org.albaross.agents4j.island;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.albaross.agents4j.extraction.AprioriMemExtraction;
import org.albaross.agents4j.extraction.HierarchicalKnowledgeBase;
import org.albaross.agents4j.extraction.StateActionPair;
import org.albaross.agents4j.learning.Experience;
import org.albaross.agents4j.learning.ReplayAgent;

public class LearningAgent {

	public static void main(String[] args) throws IOException {
		ReplayAgent<IslandPerception, IslandAction> agent = new ReplayAgent<>(IslandAction::randomAction, 2000);
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(agent));

		int r = 0;
		do {
			env.run();
			System.out.println("Round " + r + ", Rewards " + (int) env.getCumulative(0) + ", Ticks " + env.getCurrentTick());

			r++;
		} while (r < 2000000);

		List<StateActionPair<IslandPerception, IslandAction>> seq = new LinkedList<>();

		r = 0;
		do {
			env.run();
			for (Experience<IslandPerception, IslandAction> exp : agent.getLastSequence()) {
				seq.add(new StateActionPair<>(exp.getState(), exp.getAction()));
			}
			
			r++;
		} while (r < 10);

		AprioriMemExtraction<IslandPerception, IslandAction> ext = new AprioriMemExtraction<>();
		ext.setMinSupport(0.01);
		HierarchicalKnowledgeBase<IslandPerception, IslandAction> hkb = ext.extract(seq);

		System.out.println(hkb);
	}

}
