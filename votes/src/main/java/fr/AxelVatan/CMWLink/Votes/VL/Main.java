package fr.AxelVatan.CMWLink.Votes.VL;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.VL.Routes.RewardCmd;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.Common.RewardQueue;
import fr.AxelVatan.CMWLink.Votes.VL.Routes.VoteReceived;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	private @Getter RewardQueue queue;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for Velocity enabled.");
		this.queue = new RewardQueue(this, config);
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
