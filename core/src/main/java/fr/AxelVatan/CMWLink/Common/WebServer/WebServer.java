package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import express.Express;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public class WebServer {

	private @Getter ConfigFile config;
	private Express app;
	private @Getter HashMap<String, IRoute> routes;
	
	//TEST CODE
	public static void main(String a[]){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:24102/boutique/version"))
                .GET()
                .header("User", "admin")
                .header("Pwd", "changme")
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        System.out.println(response.body());
    }
	//
	
	public WebServer(ConfigFile config) {
		this.config = config;
		this.app = new Express();
		this.routes = new HashMap<String, IRoute>();
		app.all("/", (req, res) -> {
			JsonObject jsObj = new JsonObject();
			jsObj.addProperty("CODE", 200);
			jsObj.addProperty("NAME", "CraftMyWebSite_Link");
			jsObj.addProperty("VERSION", config.getVersion());
			res.send(jsObj.toString());
		});
		authRequest();
	}
	
	private void authRequest() {
		app.use((req, res) -> {
			String user = req.getHeader("User").get(0);
			String pwd = req.getHeader("Pwd").get(0);
			String userAndPwdFromHost = BCrypt.hashpw(user + ":" + pwd, BCrypt.gensalt(10));
			Boolean match = BCrypt.checkpw(config.getConfig().getUsername() + ":" + config.getConfig().getPassword(), userAndPwdFromHost);
			if(!match) {
				JsonObject jsObj = new JsonObject();
				jsObj.addProperty("CODE", 401);
				jsObj.addProperty("MESSAGE", "User not authorized to execute this request.");
				config.getLog().severe("User: " + user + " from host: " + req.getIp() + " is not authorized to execute the route: " + req.getPath());
				res.send(jsObj.toString());
			}
		});
	}
	
	public void startWebServer() {
		this.app.listen(this.config.getConfig().getPort());
		try {
			URL whatismyip = new URL("https://ip.conceptngo.fr/myIP");
			URLConnection uc = whatismyip.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: " + config.getVersion());
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			JsonObject json = new Gson().fromJson(in.readLine(), JsonObject.class);
			String ip = json.get("IP").getAsString();
			this.config.getLog().info("External IP: " + ip);
			URL checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/" + this.config.getConfig().getPort());
			uc = checkURL.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: " + config.getVersion());
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			json = new Gson().fromJson(in.readLine(), JsonObject.class);
			boolean reachable = json.get("REACHABLE").getAsBoolean();
			if(reachable) {
				this.config.getLog().info("Port " + this.config.getConfig().getPort() + " is properly forwarded and is externally accessible.");
			}
			else {
				this.config.getLog().severe("Port " + this.config.getConfig().getPort() + " is not properly forwarded.");
			}
		} catch (Exception e) {
			this.config.getLog().severe("Cannot joint API to get IP and PORT verification, maybe API is down ");
		}
	}

	public void addRoute(CMWLPackage cmwlPackage, IRoute route){
		cmwlPackage.log(Level.INFO, "Register route: /" + cmwlPackage.getRoutePrefix() + "/" + route.getRouteName());
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
		handleNonExistingRoutes();
	}

	private void handleNonExistingRoutes() {
		app.all((req, res) -> {
			JsonObject jsObj = new JsonObject();
			jsObj.addProperty("CODE", 404);
			jsObj.addProperty("MESSAGE", "Route " + req.getPath() + " not found !");
			res.send(jsObj.toString());
		});
	}
	
	public void disable() {
		this.app.stop();
	}
}
