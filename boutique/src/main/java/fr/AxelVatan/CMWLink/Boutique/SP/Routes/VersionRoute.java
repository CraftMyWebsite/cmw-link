package fr.AxelVatan.CMWLink.Boutique.SP.Routes;

import java.util.HashMap;

import fr.AxelVatan.CMWLink.Boutique.SP.Main;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class VersionRoute extends CMWLRoute<Main>{

	public VersionRoute(Main main) {
		super(main, "version", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		return new JsonBuilder("PACKAGE_NAME", this.getPlugin().getPluginName() + " for Spigot")
				.append("VERSION", this.getPlugin().getVersion()).build();
	}

}
