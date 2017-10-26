package org.albaross.agents4j.island;

import static org.albaross.agents4j.island.IslandAction.ASSEMBLE_PARTS;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_HQ;
import static org.albaross.agents4j.island.IslandAction.MOVE_TO_SITE;
import static org.albaross.agents4j.island.IslandLocation.AT_SITE;

import java.util.Arrays;

public class NaiveAgent {

	public static void main(String[] args) {
		long sum = 0;
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList((p, t) -> {
			return p.getSite() < IslandLabEnvironment.SITE_COMPLETE ? (p.getLocation() != AT_SITE ? MOVE_TO_SITE : ASSEMBLE_PARTS) : MOVE_TO_HQ;
		}));
		for (int r = 0; r < 2000000; r++) {
			env.run();
			System.out.println(env.getCurrentTick());
			sum += env.getCurrentTick();
		}
		System.out.println("ticks: " + sum / 2000000);
	}

}
