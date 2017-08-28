package org.albaross.agents4j.island;

import java.util.Random;

/**
 * 
 * @author Manuel Barbi
 * 
 */
public enum IslandAction {

	ASSEMBLE_PARTS, CHARGE_BATTERY, COVER_SITE, UNCOVER_SITE, MOVE_TO_HQ, MOVE_TO_SITE, ENTER_CAVE, LEAVE_CAVE;

	private static final Random RND = new Random();

	public static IslandAction randomAction() {
		return IslandAction.values()[RND.nextInt(IslandAction.values().length)];
	}

}
