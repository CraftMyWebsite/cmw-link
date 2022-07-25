package fr.AxelVatan.CMWLink.Common.WebServer;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			JsonElement je = JsonParser.parseString(executeRoute(req.getParams()));
			res.send(gson.toJson(je));
		}catch(Exception e) {
			e.printStackTrace();
			JsonElement je = JsonParser.parseString(new JsonBuilder("CODE", 500).append("MESSAGE", e.getMessage() + ", see console for more informations !").build());
			res.send(gson.toJson(je));
		}
		
	}
	
	public abstract String executeRoute(HashMap<String, String> params);
}
