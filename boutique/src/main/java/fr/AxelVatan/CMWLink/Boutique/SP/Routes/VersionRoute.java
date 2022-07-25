package fr.AxelVatan.CMWLink.Boutique.SP.Routes;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.SP.Main;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class VersionRoute extends CMWLRoute<Main>{

	public VersionRoute(Main main) {
		super(main, "version", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		JsonBuilder json = new JsonBuilder()
				.append("PACKAGE_NAME", this.getPlugin().getPluginName() + " for Spigot")
				.append("VERSION", this.getPlugin().getVersion());
		res.send(json.build());
	}

}
