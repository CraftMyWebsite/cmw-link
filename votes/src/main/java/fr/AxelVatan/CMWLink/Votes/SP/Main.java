package fr.AxelVatan.CMWLink.Votes.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.Common.RewardQueue;
import fr.AxelVatan.CMWLink.Votes.Common.SP.RewardQueueSP;
import fr.AxelVatan.CMWLink.Votes.SP.Routes.RewardCmd;
import fr.AxelVatan.CMWLink.Votes.SP.Routes.VoteReceived;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	private @Getter RewardQueue queue;
	
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
