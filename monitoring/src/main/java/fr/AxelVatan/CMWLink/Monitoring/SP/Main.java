package fr.AxelVatan.CMWLink.Monitoring.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "Server Monitoring for Spigot enabled.");
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		
	}

}
