package org.albaross.agents4j.island;

import static org.albaross.agents4j.island.IslandAction.ASSEMBLE_PARTS;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_HQ;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_SITE;
import static org.albaross.agents4j.island.IslandLocation.AT_SITE;

import java.util.Arrays;

public class NaiveAgent {

	public static void main(String[] args) {
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList((p) -> {
			return !p.isComplete() ? (p.getLocation() != AT_SITE ? MOVE_TO_SITE : ASSEMBLE_PARTS) : MOVE_TO_HQ;
		}));
		env.run();
		System.out.println("rewards: " + env.getCumulative(0));
	}

}
