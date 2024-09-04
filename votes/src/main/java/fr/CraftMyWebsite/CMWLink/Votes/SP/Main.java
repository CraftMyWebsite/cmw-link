package fr.CraftMyWebsite.CMWLink.Votes.SP;

import java.util.logging.Level;

import fr.CraftMyWebsite.CMWLink.Common.Config.ConfigFile;
import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import fr.CraftMyWebsite.CMWLink.Votes.Common.SP.RewardQueueSP;
import fr.CraftMyWebsite.CMWLink.Votes.SP.Routes.VoteReceived;
import fr.CraftMyWebsite.CMWLink.Votes.SP.Routes.RewardCmd;
import lombok.Getter;

public class Main extends CMWLPackage {

	private @Getter Config config;
	private @Getter RewardQueue queue;
	private @Getter ConfigFile configFile;

	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for Spigot enabled.");
		this.config = new Config();
		this.queue = new RewardQueueSP(this, config);
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VoteReceived(this));
		this.addRoute(new RewardCmd(this));
	}
}
