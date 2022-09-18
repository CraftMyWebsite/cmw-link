package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import express.Express;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Injector;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.RouteMatcher;
import lombok.Getter;

public class WebServer {

	private @Getter ConfigFile config;
	private Express app;
	private @Getter HashMap<String, IRoute> routes;
	private @Getter RouteMatcher router;
	private Injector injector;
	
	//TEST CODE
	public static void main(String a[]){
		/*ExecutorService executor = Executors.newFixedThreadPool(50);
		for (int i = 0; i < 1; i++) {
			Runnable worker = new MyRunnable(i);
			try {
				executor.execute(worker);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		System.out.println("RESULT: " + BCrypt.hashpw("test", BCrypt.gensalt()));
		System.out.println("RESULT: " + BCrypt.checkpw("test", "$2a$10$eBfu1aIV0jH45fIoPpg2pOibq/MtB9X50bt/XV5GsLTLtWE/YSb0u"));
    }
	//
	
	public WebServer(ConfigFile config) {
		this.config = config;
		this.app = new Express();
		this.routes = new HashMap<String, IRoute>();
		this.router = new RouteMatcher();
		this.injector = new Injector(this);
		app.all("/", (req, res) -> {
			JsonBuilder json = new JsonBuilder()
					.append("CODE", 200)
					.append("NAME", "CraftMyWebSite_Link")
					.append("VERSION", config.getVersion());
			res.send(json.build());
		});
		authRequest();
	}
	
	private void authRequest() {
		app.use((req, res) -> {
			System.out.println("HEADERS: " + req.getHeaders().entrySet());
			/*String user = req.getHeader("User").get(0);
			String pwd = req.getHeader("Pwd").get(0);
			String userAndPwdFromHost = BCrypt.hashpw(user + ":" + pwd, BCrypt.gensalt(10));
			Boolean match = BCrypt.checkpw(config.getConfig().getUsername() + ":" + config.getConfig().getPassword(), userAndPwdFromHost);
			if(!match) {
				JsonBuilder json = new JsonBuilder()
						.append("CODE", 401)
						.append("MESSAGE", "User not authorized to execute this request.");
				res.send(json.build());
				config.getLog().severe("User: " + user + " from host: " + req.getIp() + " is not authorized to execute the route: " + req.getPath());
				res.send(json.build());
			}*/
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
			URL checkURL;
			if(this.getConfig().getConfig().isBindToDefaultPort()) {
				checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/25565");
			}else {
				checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/" + this.config.getConfig().getPort());
			}
			uc = checkURL.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: " + config.getVersion());
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			json = new Gson().fromJson(in.readLine(), JsonObject.class);
			boolean reachable = json.get("REACHABLE").getAsBoolean();
			if(reachable) {
				this.config.getLog().info("Port " + (this.getConfig().getConfig().isBindToDefaultPort() ? "25565" : this.config.getConfig().getPort()) + " is properly forwarded and is externally accessible.");
			}
			else {
				this.config.getLog().severe("Port " + (this.getConfig().getConfig().isBindToDefaultPort() ? "25565" : this.config.getConfig().getPort()) + " is not properly forwarded.");
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
			JsonBuilder json = new JsonBuilder()
					.append("CODE", 404)
					.append("MESSAGE", "Route " + req.getPath() + " not found !");
			res.send(json.build());
		});
	}
	
	public void disable() {
		this.app.stop();
		this.injector.close();
	}
}
