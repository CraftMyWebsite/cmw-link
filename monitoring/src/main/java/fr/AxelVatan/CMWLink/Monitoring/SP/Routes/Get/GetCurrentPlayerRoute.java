package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;

public class GetCurrentPlayerRoute extends CMWLRoute<Main>{

	public GetCurrentPlayerRoute(Main plugin) {
		super(plugin, "currentPlayers", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		
	}
	
}
