package fr.AxelVatan.CMWLink.Monitoring.SP;

import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetAllInfosRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetCurrentPlayerRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetCurrentRamRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetMaxPlayerRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetMaxRamRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetMotdRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetSettingsRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetTPSRoute;
import fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get.GetVersionRoute;
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
		this.addRoute(new GetAllInfosRoute(this));
		this.addRoute(new GetCurrentPlayerRoute(this));
		this.addRoute(new GetCurrentRamRoute(this));
		this.addRoute(new GetMaxPlayerRoute(this));
		this.addRoute(new GetMaxRamRoute(this));
		this.addRoute(new GetMotdRoute(this));
		this.addRoute(new GetSettingsRoute(this));
		this.addRoute(new GetTPSRoute(this));
		this.addRoute(new GetVersionRoute(this));
		//POST
	}

}
