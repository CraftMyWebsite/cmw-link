package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import java.util.HashMap;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetMotdRoute extends CMWLRoute<Main>{

	public GetMotdRoute(Main plugin) {
		super(plugin, "motd", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		return new JsonBuilder("CODE", 200).append("MOTD", this.getPlugin().getServerInfos().getMotd())
				.append("VERSION", this.getPlugin().getServerInfos().getVersion()).build();
	}

}
