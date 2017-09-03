package org.albaross.agents4j.island;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.albaross.agents4j.core.Agent;
import org.albaross.agents4j.extraction.AprioriMemExtraction;
import org.albaross.agents4j.extraction.HierarchicalKnowledgeBase;
import org.albaross.agents4j.extraction.KnowledgeExtraction;
import org.albaross.agents4j.extraction.Premise;
import org.albaross.agents4j.extraction.Rule;
import org.albaross.agents4j.extraction.StateActionPair;
import org.albaross.agents4j.extraction.Utils;
import org.albaross.agents4j.learning.QTable;

public class TestLoadQTable {

	private static Pattern REG = Pattern.compile(
			"\\[(\\d),\\s(\\w+),\\s(\\w+)\\s\\-\\>\\s(\\w+)\\sin\\s(\\d)\\sticks,\\ssite\\s(not\\s)?secured,\\ssite\\s(not\\s)?complete\\]");

	public static void main(String[] args) throws IOException {
		QTable<String, String> table = QTable.load(new File("C:/Workspace/agent4j/agents4j-island/qtables/qtable_102274_-1661.0.json"),
				String.class, String.class);

		Matcher m;
		IslandPerception state;
		IslandAction action;
		Set<StateActionPair<IslandPerception, IslandAction>> seq = new HashSet<>();

		for (String p : table.states()) {
			m = REG.matcher(p);
			if (m.find()) {
				state = new IslandPerception(Integer.parseInt(m.group(1)), IslandLocation.valueOf(m.group(2)), IslandWeather.valueOf(m.group(3)),
						IslandWeather.valueOf(m.group(4)), Integer.parseInt(m.group(5)), m.group(6) == null, m.group(7) == null);
				action = IslandAction.valueOf(table.getBestAction(p));
				seq.add(new StateActionPair<>(state, action));
			}
		}

		KnowledgeExtraction<IslandPerception, IslandAction> ext = new AprioriMemExtraction<>(IslandPerception.class, (a1, a2) -> {
			return a1.compareTo(a2);
		});

		long start, end;
		start = System.currentTimeMillis();

		HierarchicalKnowledgeBase<IslandAction> hkb = ext.extract(seq);

		end = System.currentTimeMillis();
		System.out.println(String.valueOf(seq.size()) + " in " + String.valueOf(end - start));

		System.out.println(hkb);
		System.out.println("done");

		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(new HKBAgent(hkb)));

		for (int i = 0; i < 10000; i++) {
			System.out.println("Round " + i);
			env.run();
			System.out.println("Rewards:" + env.getCumulative(0));
		}
	}

	private static class HKBAgent implements Agent<IslandPerception, IslandAction> {

		private final HierarchicalKnowledgeBase<IslandAction> hkb;
		private static final Random RND = new Random();

		public HKBAgent(HierarchicalKnowledgeBase<IslandAction> hkb) {
			this.hkb = hkb;
		}

		@Override
		public IslandAction generateAction(IslandPerception perception) {
			List<Rule<IslandAction>> actions = hkb.reasoning(Utils.stateToPremise(perception));
			Rule<IslandAction> rule = actions.size() == 1 ? actions.get(0) : actions.get(RND.nextInt(actions.size()));
			return rule.getConclusion();
		}

	}

}
