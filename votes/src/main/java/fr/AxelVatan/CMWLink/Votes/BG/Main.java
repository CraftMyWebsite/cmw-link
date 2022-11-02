package fr.AxelVatan.CMWLink.Votes.BG;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.BG.Routes.VoteReceived;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.Common.RewardQueue;
import fr.AxelVatan.CMWLink.Votes.BG.Routes.RewardCmd;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	private @Getter RewardQueue queue;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for BungeeCord enabled.");
		this.config = new Config();
		this.setPackageConfig(config);
		this.loadPackageConfig(config);
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
