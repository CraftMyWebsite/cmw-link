package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetMaxPlayersRoute extends CMWLRoute<Main>{

	public GetMaxPlayersRoute(Main plugin) {
		super(plugin, "maxPlayers", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send(new JsonBuilder("CODE", 200).append("MAX_PLAYERS", this.getPlugin().getServerInfos().getMaxPlayers()).build());
	}

}
