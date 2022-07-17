package fr.AxelVatan.CMWLink.Boutique.SP.Routes;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.SP.Main;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class VersionRoute extends CMWLRoute<Main>{

	public VersionRoute(Main main) {
		super(main, "version", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send(this.getPlugin().getPluginName() + " spigot version " + this.getPlugin().getVersion());
	}

}
