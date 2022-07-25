package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetMotdRoute extends CMWLRoute<Main>{

	public GetMotdRoute(Main plugin) {
		super(plugin, "motd", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send(new JsonBuilder("CODE", 200).append("MOTD", this.getPlugin().getServerInfos().getMotd())
				.append("VERSION", this.getPlugin().getServerInfos().getVersion()).build());
	}

}
