package org.albaross.agents4j.island;

import static org.albaross.agents4j.island.IslandLocation.AT_HQ;
import static org.albaross.agents4j.island.IslandLocation.AT_SITE;
import static org.albaross.agents4j.island.IslandLocation.IN_CAVE;
import static org.albaross.agents4j.island.IslandLocation.ON_THE_WAY_1;
import static org.albaross.agents4j.island.IslandLocation.ON_THE_WAY_2;
import static org.albaross.agents4j.island.IslandLocation.ON_THE_WAY_3;
import static org.albaross.agents4j.island.IslandWeather.CLOUDS;
import static org.albaross.agents4j.island.IslandWeather.STORM_OR_RAIN;
import static org.albaross.agents4j.island.IslandWeather.SUN;
import static org.albaross.agents4j.island.IslandWeather.THUNDERSTORM;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.albaross.agents4j.core.Agent;
import org.albaross.agents4j.learning.RLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Manuel Barbi
 *
 */
public class IslandLabEnvironment extends RLEnvironment<IslandPerception, IslandAction> {

	private static final Logger LOG = LoggerFactory.getLogger(IslandLabEnvironment.class);

	protected Random rnd = new Random();

	protected int[] site;
	protected boolean secured;
	protected boolean complete;

	protected IslandWeather weather;
	protected IslandWeather prediction;
	protected int change;
	protected IslandLocation lightning;

	protected int[] batteries;
	protected IslandLocation[] locations;
	protected boolean[] operable;

	public IslandLabEnvironment(List<Agent<IslandPerception, IslandAction>> agents) {
		super(agents);
		this.site = new int[16];
		this.batteries = new int[agents.size()];
		this.locations = new IslandLocation[agents.size()];
		this.operable = new boolean[agents.size()];
	}

	@Override
	public void runEnvironment() {
		super.runEnvironment();
		
		this.lightning = null;
		Arrays.fill(this.operable, true);
		this.change--;

		if (change == 0) {
			this.weather = (rnd.nextDouble() > 0.2) ? this.prediction : generateWeather();
			this.prediction = generateWeather();
			this.change = 4;
		}

		if (weather == THUNDERSTORM) {
			switch (rnd.nextInt(16)) {
			case 0:
				this.lightning = AT_SITE;
				break;
			case 1:
				this.lightning = ON_THE_WAY_1;
				break;
			case 2:
				this.lightning = ON_THE_WAY_2;
				break;
			case 3:
				this.lightning = ON_THE_WAY_3;
				break;
			}
		}
	}

	protected IslandWeather generateWeather() {
		switch (rnd.nextInt(4)) {
		case 1:
			return SUN;
		case 2:
			return STORM_OR_RAIN;
		case 3:
			return THUNDERSTORM;
		default:
			return CLOUDS;
		}
	}

	@Override
	public IslandPerception createPerception(int agentId) {
		return new IslandPerception((int) Math.ceil(batteries[agentId] / 8.0), locations[agentId], weather, prediction, secured, complete);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void executeAction(int agentId, IslandAction action) {
		boolean slow = false;
		boolean shelter = false;

		switch (weather) {
		case STORM_OR_RAIN:
		case THUNDERSTORM:
			slow = true;
		}

		switch (locations[agentId]) {
		case AT_HQ:
		case IN_CAVE:
			shelter = true;
		}

		switch (locations[agentId]) {
		case AT_HQ:
			switch (action) {
			case CHARGE_BATTERY:
				batteries[agentId] = Math.min(batteries[agentId] + 8, 32);
				break;
			case MOVE_TO_SITE:
				locations[agentId] = (slow ? ON_THE_WAY_1 : ON_THE_WAY_2);
				break;
			}
			break;
		case AT_SITE:
			switch (action) {
			case ASSEMBLE_PARTS: {
				int part = 0;
				int rem = (slow ? 2 : 4);

				while (rem > 0 && part < site.length) {
					if (site[part] < 8) {
						site[part]++;
						rem--;
					} else {
						part++;
					}
				}

				this.complete = determineComplete();
			}
				break;
			case COVER_SITE:
				secured = true;
				break;
			case UNCOVER_SITE:
				secured = false;
				break;
			case MOVE_TO_HQ:
				locations[agentId] = (slow ? ON_THE_WAY_3 : ON_THE_WAY_2);
				break;
			case ENTER_CAVE:
				locations[agentId] = IN_CAVE;
				break;
			}
			break;
		case IN_CAVE:
			switch (action) {
			case LEAVE_CAVE:
				locations[agentId] = AT_SITE;
				break;
			}
			break;
		case ON_THE_WAY_1:
			switch (action) {
			case MOVE_TO_HQ:
				locations[agentId] = AT_HQ;
				break;
			case MOVE_TO_SITE:
				locations[agentId] = (slow ? ON_THE_WAY_2 : ON_THE_WAY_3);
				break;
			}
			break;
		case ON_THE_WAY_2:
			switch (action) {
			case MOVE_TO_HQ:
				locations[agentId] = (slow ? ON_THE_WAY_1 : AT_HQ);
				break;
			case MOVE_TO_SITE:
				locations[agentId] = (slow ? ON_THE_WAY_3 : AT_SITE);
				break;
			}
			break;
		case ON_THE_WAY_3:
			switch (action) {
			case MOVE_TO_HQ:
				locations[agentId] = (slow ? ON_THE_WAY_2 : ON_THE_WAY_1);
				break;
			case MOVE_TO_SITE:
				locations[agentId] = AT_SITE;
				break;
			}
			break;
		default:
			LOG.warn("unhandled action");
		}

		executeAftermath(agentId, shelter);
	}

	protected void executeAftermath(int agentId, boolean shelter) {
		// charge battery with solar panel
		if (weather == SUN && !shelter)
			batteries[agentId] = Math.min(batteries[agentId] + 2, 32);

		// lightning at the site
		if (!secured && AT_SITE.equals(lightning)) {
			int part = rnd.nextInt(site.length);
			LOG.debug("part {} was damaged", part);
			site[part] = -8;
			this.complete = false;
		}

		// agent is struck by lightning
		if (locations[agentId].equals(lightning)) {
			LOG.debug("agent {} was damaged", agentId);
			operable[agentId] = false;
		}

		if (batteries[agentId] == 0) {
			LOG.debug("agent {} ran out of energy", agentId);
			operable[agentId] = false;
		}

		// bring agent to HQ for repair and recharge battery
		if (!operable[agentId]) {
			locations[agentId] = AT_HQ;
			batteries[agentId] = 32;
		}

		// discharge battery
		batteries[agentId] = Math.max(batteries[agentId] - 1, 0);
	}

	protected boolean determineComplete() {
		for (int p : site) {
			if (p < 8)
				return false;
		}

		return true;
	}

	@Override
	protected double getReward(int agentId) {
		if (!operable[agentId])
			return -100;

		return terminationCriterion(agentId) ? 0 : -1;
	}

	@Override
	public boolean terminationCriterion(int agentId) {
		// agent has to return to HQ after finishing work
		return this.complete && locations[agentId] == AT_HQ;
	}

	@Override
	public void reboot() {
		super.reboot();

		Arrays.fill(this.site, 0);
		this.secured = true;
		this.complete = false;

		this.weather = CLOUDS;
		this.prediction = generateWeather();
		this.change = 5;
		this.lightning = null;

		Arrays.fill(this.batteries, 31);
		Arrays.fill(this.locations, AT_HQ);
		Arrays.fill(this.operable, true);
	}

}
