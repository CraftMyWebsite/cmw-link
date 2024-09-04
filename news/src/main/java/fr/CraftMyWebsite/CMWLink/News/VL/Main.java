package fr.CraftMyWebsite.CMWLink.News.VL;

import java.util.logging.Level;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "News for Velocity enabled.");
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		
	}
}
