package fr.CraftMyWebsite.CMWLink.Votes.VL;

import java.util.logging.Level;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import fr.CraftMyWebsite.CMWLink.Votes.Common.VL.RewardQueueVL;
import fr.CraftMyWebsite.CMWLink.Votes.VL.Routes.RewardCmd;
import fr.CraftMyWebsite.CMWLink.Votes.VL.Routes.VoteReceived;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	private @Getter RewardQueue queue;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for Velocity enabled.");
		this.queue = new RewardQueueVL(this, config);
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
