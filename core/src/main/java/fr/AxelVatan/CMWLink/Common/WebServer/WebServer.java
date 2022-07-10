package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import express.Express;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;

public class WebServer {

	private ConfigFile config;
	private Express app;
	private @Getter HashMap<String, IRoute> routes;

	public WebServer(ConfigFile config) {
		this.config = config;
		this.app = new Express();
		this.routes = new HashMap<String, IRoute>();
		app.all("/", (req, res) -> {
			//MAYBE CHANGE JSON LIB LATER...
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("SUCCESS", 200);
			map.put("NAME", "CraftMyWebSite_Link");
			map.put("VERSION", 1.0);
			JSONObject jo = new JSONObject(map);
			res.send(jo.toString());
		});
	}

	public void startWebServer() {
		this.app.listen(this.config.getConfig().getPort());
		try {
			URL whatismyip = new URL("https://ip.conceptngo.fr/myIP");
			URLConnection uc = whatismyip.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: 1.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String ip = in.readLine();
			this.config.getLog().info("External IP: " + ip);
			URL checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/" + this.config.getConfig().getPort());
			uc = checkURL.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: 1.0");
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String code = in.readLine();
			if(code.equalsIgnoreCase("200")) {
				this.config.getLog().info("Port " + this.config.getConfig().getPort() + " is properly forwarded and is externally accessible.");
			}
			else {
				this.config.getLog().severe("Port " + this.config.getConfig().getPort() + " is not properly forwarded.");
			}
		} catch (Exception e) {
			this.config.getLog().severe("Cannot joint API to get IP and PORT verification: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	public void addRoute(IRoute route){
		this.routes.put(route.getRouteName(), route);
	}

	public void removeRoute(IRoute route){
		this.routes.remove(route.getRouteName());
	}
	
	public void createRoutes() {
		for(IRoute route : this.routes.values()) {
			switch(route.getRouteType()) {
			case GET:
				//UGLY BUT IL BE MODIFIED LATER
				app.get("/" + route.getRouteName(), (req, res) -> {
					route.execute(req, res);
				});
				break;
			case POST:
				
				break;
			case PUT:
				
				break;
			}
		}
	}
}
