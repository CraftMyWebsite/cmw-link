package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetMaxPlayerRoute extends CMWLRoute<Main>{

	public GetMaxPlayerRoute(Main plugin) {
		super(plugin, "maxPlayers", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		
	}

}
