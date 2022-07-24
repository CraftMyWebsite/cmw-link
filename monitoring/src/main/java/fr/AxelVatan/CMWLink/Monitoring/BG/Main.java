package fr.AxelVatan.CMWLink.Monitoring.BG;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "Server Monitoring for BungeeCord enabled.");
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		
	}

}
