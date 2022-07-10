package fr.AxelVatan.CMWLink.Common.WebServer;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class VersionRoute extends CMWLRoute<CMWLPackage>{

	public VersionRoute(CMWLPackage plugin) {
		super(plugin, plugin.getPluginName() + "version", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send("VERSION OF " + this.getPlugin().getPluginName() + " IS " + this.getPlugin().getVersion());
	}
}
