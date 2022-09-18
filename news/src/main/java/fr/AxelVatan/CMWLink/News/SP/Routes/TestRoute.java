package fr.AxelVatan.CMWLink.News.SP.Routes;

import java.util.HashMap;
import java.util.logging.Level;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.News.SP.Main;

public class TestRoute extends CMWLRoute<Main>{

	public TestRoute(Main plugin) {
		super(plugin, "test/:testText", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String testText = params.get("testText");
		this.getPlugin().log(Level.INFO, "MESSAGE_RECEIVED: " + testText);
		JsonBuilder json = new JsonBuilder();
		json.append("MESSAGE_RECEIVED", testText);
		return json.build();
	}
}
