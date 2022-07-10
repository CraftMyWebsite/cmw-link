package fr.AxelVatan.CMWLink.Boutique;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique enabled.");
		
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		this.addRoute(new Version(this));
	}


}
