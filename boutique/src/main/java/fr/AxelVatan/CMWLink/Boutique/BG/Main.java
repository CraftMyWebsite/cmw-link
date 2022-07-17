package fr.AxelVatan.CMWLink.Boutique.BG;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Boutique.BG.Routes.TestGive;
import fr.AxelVatan.CMWLink.Boutique.BG.Routes.VersionRoute;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import net.md_5.bungee.api.ProxyServer;

public class Main extends CMWLPackage {

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique for BungeeCord enabled.");
		ProxyServer.getInstance().registerChannel("cmw-boutique");
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
