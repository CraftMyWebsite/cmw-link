package fr.AxelVatan.CMWLink.Monitoring.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetBanListRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetCurrentPlayersRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetMaxPlayersRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetMotdRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetOpListRoute;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter ServerInfos serverInfos;
	
	@Override
	public void enable() {
		this.log(Level.INFO, "Server Monitoring for Spigot enabled.");
		this.serverInfos = new ServerInfos(this);
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		//GET
		//PLAYERS
		this.addRoute(new GetCurrentPlayersRoute(this));
		this.addRoute(new GetMaxPlayersRoute(this));
		//SERVER
		this.addRoute(new GetMotdRoute(this));
		this.addRoute(new GetOpListRoute(this));
		this.addRoute(new GetBanListRoute(this));
		//POST
	}

}
