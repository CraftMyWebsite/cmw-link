package fr.AxelVatan.CMWLink.Votes;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class VersionRoute extends CMWLRoute<Main>{

	public VersionRoute(Main main) {
		super(main, "vote", "version", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send(this.getPlugin().getPluginName() + " version " + this.getPlugin().getVersion());
	}

}
