package fr.AxelVatan.CMWLink.Votes.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.SP.Routes.VoteReceived;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for Spigot enabled.");
		this.setPackageConfig(new Config());
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VoteReceived(this));
		//this.addRoute(new );
	}
}
