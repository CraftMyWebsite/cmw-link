package fr.AxelVatan.CMWLink.Boutique.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Boutique.TestGive;
import fr.AxelVatan.CMWLink.Boutique.VersionRoute;
import fr.AxelVatan.CMWLink.Boutique.Methods.IBoutiqueMethods;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter IBoutiqueMethods methods;

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique enabled.");
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VersionRoute(this));
		this.addRoute(new TestGive(this));
	}
}
