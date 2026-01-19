package fr.CraftMyWebsite.CMWLink.Common.Utils;

import java.util.logging.Logger;

import lombok.Getter;

public class Utils {

	private final Logger log;
	private final StartingFrom startingFrom;

	private @Getter OfflinePlayerLoader offlinePlayerLoader;

	public Utils(Logger log, StartingFrom startingFrom) {
		this.log = log;
		this.startingFrom = startingFrom;
	}

	public boolean init() {
		switch(startingFrom) {
		case BUNGEECORD:
			return true;
		case SPIGOT:
			offlinePlayerLoader = new OfflinePlayerLoader();
			return true;
		case VELOCITY:
			return true;
		default:
			return false;
		}
	}
}
