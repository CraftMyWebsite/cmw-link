package fr.AxelVatan.CMWLink.Votes.BG;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.BG.Routes.VoteReceived;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter Config config;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for BungeeCord enabled.");
		this.config = new Config();
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VoteReceived(this));
	}

}
