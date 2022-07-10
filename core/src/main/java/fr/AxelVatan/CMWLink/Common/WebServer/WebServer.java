package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import express.Express;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
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
			JsonObject jsObj = new JsonObject();
			jsObj.addProperty("CODE", 200);
			jsObj.addProperty("NAME", "CraftMyWebSite_Link");
			jsObj.addProperty("VERSION", 1.0);
			res.send(jsObj.toString());
		});
		app.use((req, res) ->{
			if(routes.containsKey(req.getPath())) {
				if(config.getConfig().isLogRequests()) {
					config.getLog().info("Host " + req.getIp() + " requested route " + req.getPath());
				}
			}else {
				JsonObject jsObj = new JsonObject();
				jsObj.addProperty("CODE", 404);
				jsObj.addProperty("ERROR", "Route " + req.getPath() + " does not exist.");
				res.send(jsObj.toString());
				if(config.getConfig().isLogRequests() && !req.getPath().contains("favicon.ico")) {
					config.getLog().severe("Route " + req.getPath() + " requested by " + req.getIp() + " does not exist.");
				}
			}
		});
	}

	public void startWebServer() {
		this.app.listen(this.config.getConfig().getPort());
		try {
			URL whatismyip = new URL("https://ip.conceptngo.fr/myIP");
			URLConnection uc = whatismyip.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: 1.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			JsonObject ipJson = new Gson().fromJson(in.readLine(), JsonObject.class);
			String ip = ipJson.get("IP").getAsString();
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

	public void addRoute(CMWLPackage cmwlPackage, IRoute route){
		cmwlPackage.log(Level.INFO, "Register route: " + cmwlPackage.getRoutePrefix() + "/" + route.getRouteName());
		this.routes.put("/" + cmwlPackage.getRoutePrefix() + "/" + route.getRouteName(), route);
	}

	public void removeRoute(CMWLPackage cmwlPackage, IRoute route){
		this.routes.remove("/" + cmwlPackage.getRoutePrefix() + "/" + route.getRouteName());
	}

	public void createRoutes() {
		for(Entry<String, IRoute> entry : this.routes.entrySet()) {
			String routeName = entry.getKey();
			IRoute route = entry.getValue();
			switch(route.getRouteType()) {
			case GET:
				app.get(routeName, (req, res) -> {
					route.execute(req, res);
				});
				break;
			case POST:
				app.post(routeName, (req, res) -> {
					route.execute(req, res);
				});
				break;
			case PUT:
				app.put(routeName, (req, res) -> {
					route.execute(req, res);
				});
				break;
			}
		}
		routes.put("/", null);
	}

	public void disable() {
		this.app.stop();
	}
}
