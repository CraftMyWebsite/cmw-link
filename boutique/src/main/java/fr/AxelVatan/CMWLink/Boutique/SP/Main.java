package fr.AxelVatan.CMWLink.Boutique.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Boutique.SP.Routes.TestGive;
import fr.AxelVatan.CMWLink.Boutique.SP.Routes.VersionRoute;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique for Spigot enabled.");
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		if(!this.isUseProxy()) {
			this.addRoute(new VersionRoute(this));
			this.addRoute(new TestGive(this));
		}
	}
}
