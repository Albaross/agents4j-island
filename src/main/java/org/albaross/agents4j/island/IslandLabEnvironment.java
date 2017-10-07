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
import org.albaross.agents4j.core.BasicEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Manuel Barbi
 *
 */
public class IslandLabEnvironment extends BasicEnvironment<IslandPerception, IslandAction> {

	private static final Logger LOG = LoggerFactory.getLogger(IslandLabEnvironment.class);

	protected Random rnd = new Random();

	public static final int SITE_COMPLETE = 8 * 8;
	protected int site;
	protected boolean secured;

	protected IslandWeather weather;
	protected IslandWeather prediction;
	protected int change;
	protected IslandLocation lightning;

	protected int[] batteries;
	protected IslandLocation[] locations;
	protected boolean[] agentOperable;
	protected boolean[] componentAssembled;

	public IslandLabEnvironment(List<Agent<IslandPerception, IslandAction>> agents) {
		super(agents);
		this.site = 0;
		this.secured = false;

		this.weather = CLOUDS;
		this.prediction = IslandWeather.CLOUDS;
		this.change = 5;

		this.batteries = new int[agents.size()];
		this.locations = new IslandLocation[agents.size()];
		this.agentOperable = new boolean[agents.size()];
		this.componentAssembled = new boolean[agents.size()];
	}

	@Override
	public void runEnvironment() {
		this.lightning = null;
		Arrays.fill(this.agentOperable, true);
		Arrays.fill(this.componentAssembled, false);
		this.change--;

		if (change == 0) {
			this.weather = (rnd.nextDouble() > 0.2) ? this.prediction : generateWeather();
			this.prediction = generateWeather();
			this.change = 4;
		}

		if (weather == THUNDERSTORM) {
			switch (rnd.nextInt(12)) {
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
		return new IslandPerception(site / 8, secured, batteries[agentId] / 16, locations[agentId], weather, prediction);
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
			case ASSEMBLE_PARTS:
				if (this.site < SITE_COMPLETE) {
					int oldCount = this.site % 8;
					this.site = Math.min(this.site + (slow ? 1 : 2), SITE_COMPLETE);
					if ((this.site % 8) > oldCount)
						componentAssembled[agentId] = true;
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
		if (!secured && this.site < SITE_COMPLETE && AT_SITE.equals(lightning)) {
			int damage = rnd.nextInt(16);
			this.site = Math.max(this.site - damage, 0);
			LOG.debug("site was damaged");
		}

		// agent is struck by lightning
		if (locations[agentId].equals(lightning)) {
			LOG.debug("agent {} was damaged", agentId);
			agentOperable[agentId] = false;
		}

		if (batteries[agentId] == 0) {
			LOG.debug("agent {} ran out of energy", agentId);
			agentOperable[agentId] = false;
		}

		// bring agent to HQ for repair and recharge battery
		if (!agentOperable[agentId]) {
			locations[agentId] = AT_HQ;
			batteries[agentId] = 32;
		}

		// discharge battery
		batteries[agentId] = Math.max(batteries[agentId] - 1, 0);
	}

	@Override
	public double getReward(int agentId) {
		if (!agentOperable[agentId])
			return -100;

		if (!secured && AT_SITE.equals(lightning)) {
			return -10;
		}

		if (terminationCriterion(agentId))
			return 100;

		if (componentAssembled[agentId])
			return 0;

		return -1;
	}

	@Override
	public boolean terminationCriterion(int agentId) {
		// agent has to return to HQ after finishing work
		return this.site == SITE_COMPLETE && locations[agentId] == AT_HQ;
	}

	@Override
	public void reboot() {
		super.reboot();

		this.site = 0;
		this.secured = true;

		this.weather = CLOUDS;
		this.prediction = generateWeather();
		this.change = 5;
		this.lightning = null;

		Arrays.fill(this.batteries, 31);
		Arrays.fill(this.locations, AT_HQ);
		Arrays.fill(this.agentOperable, true);
	}

}
