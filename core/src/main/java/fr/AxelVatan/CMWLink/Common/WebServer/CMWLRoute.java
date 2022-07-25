package fr.AxelVatan.CMWLink.Common.WebServer;

import java.util.HashMap;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public abstract class CMWLRoute<PluginType extends CMWLPackage> implements IRoute{
	
	private @Getter PluginType plugin;
	private @Getter String routeName;
	private @Getter RouteType routeType;
	
	public CMWLRoute(PluginType plugin, String routeName, RouteType routeType) {
		this.plugin = plugin;
		this.routeName = routeName;
		this.routeType = routeType;
	}

	@Override
	public void execute(Request req, Response res) {
		try {
			res.send(executeRoute(req.getParams()));
		}catch(Exception e) {
			e.printStackTrace();
			res.send(new JsonBuilder("CODE", 500).append("MESSAGE", e.getMessage() + ", see console for more informations !").build());
		}
		
	}
	
	public abstract String executeRoute(HashMap<String, String> params);
}
