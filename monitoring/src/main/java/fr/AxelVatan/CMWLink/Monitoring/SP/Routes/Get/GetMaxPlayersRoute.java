package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import java.util.HashMap;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetMaxPlayersRoute extends CMWLRoute<Main>{

	public GetMaxPlayersRoute(Main plugin) {
		super(plugin, "maxPlayers", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		return new JsonBuilder("CODE", 200).append("MAX_PLAYERS", this.getPlugin().getServerInfos().getMaxPlayers()).build();
	}

}
