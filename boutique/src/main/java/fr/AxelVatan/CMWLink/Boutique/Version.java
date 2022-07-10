package fr.AxelVatan.CMWLink.Boutique;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class Version extends CMWLRoute<Main>{

	public Version(Main main) {
		super(main, "boutique", "version", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		res.send("VERISON 1.0 WHL");
	}

}
