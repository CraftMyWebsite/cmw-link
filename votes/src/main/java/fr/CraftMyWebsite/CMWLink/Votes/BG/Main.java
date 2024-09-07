package fr.CraftMyWebsite.CMWLink.Votes.BG;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.BG.Routes.RewardCmd;
import fr.CraftMyWebsite.CMWLink.Votes.BG.Routes.VoteReceived;
import fr.CraftMyWebsite.CMWLink.Votes.Common.BG.RewardQueueBG;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	private @Getter RewardQueue queue;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for BungeeCord enabled.");

		this.config = new Config(this.getMainFolder(), Logger.getAnonymousLogger());
		this.queue = new RewardQueueBG(this, config);
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
