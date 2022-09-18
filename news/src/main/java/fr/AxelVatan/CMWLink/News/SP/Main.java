package fr.AxelVatan.CMWLink.News.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.News.SP.Routes.TestRoute;

public class Main extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "News for Spigot enabled.");
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		this.addRoute(new TestRoute(this));
	}
}
