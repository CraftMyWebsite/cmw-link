package fr.AxelVatan.CMWLink.Common.WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import express.Express;
import express.utils.Status;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Common.Utils.StartingFrom;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Injector;
import fr.AxelVatan.CMWLink.Common.WebServer.Router.RouteMatcher;
import lombok.Getter;

public class WebServer {

	/*                 ___  ___
         \  \  /`\ \  \ \  \ \  \
          \__\ \__\ \__' \__' \__\
           \  \ \  \ \    \     \
            \  \ \  \ \    \     \
                    ___         ___  ___
\  \  /`\ \    \    \  \ \    \ \    \    \.  \
 \__\ \__\ \    \    \  \ \  . \ \__  \__  \`\ \
  \  \ \  \ \    \    \  \ \ |`\\ \    \    \ `\\
   \  \ \  \ \___ \___ \__\ \|  `\ \___ \___ \  `\

	 */

	private @Getter ConfigFile config;
	private Express app;
	private @Getter HashMap<String, IRoute> routes;
	private @Getter RouteMatcher router;
	private Injector injector;

	//TEST CODE
	public static void main(String a[]){
		ExecutorService executor = Executors.newFixedThreadPool(500);
		for (int i = 0; i < 100000; i++) {
			Runnable worker = new MyRunnable(i);
			try {
				executor.execute(worker);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		System.out.println("\nFinished all threads");
		/*System.out.println("RESULT: " + BCrypt.hashpw("test", BCrypt.gensalt()));
		System.out.println("RESULT: " + BCrypt.checkpw("test", "$2a$10$eBfu1aIV0jH45fIoPpg2pOibq/MtB9X50bt/XV5GsLTLtWE/YSb0u"));*/
	}

	public WebServer(ConfigFile config) {
		this.config = config;
		this.app = new Express();
		this.routes = new HashMap<String, IRoute>();
		this.router = new RouteMatcher();
		if(config.getStartingFrom().equals(StartingFrom.SPIGOT)) {
			if(config.getSettings().isBindToDefaultPort()){
				this.injector = new Injector(this);
			}
		}
		app.all("/", (req, res) -> {
			JsonBuilder json = new JsonBuilder()
					.append("CODE", 200)
					.append("NAME", "CraftMyWebSite_Link")
					.append("VERSION", config.getVersion());
			res.setStatus(Status._200);
			res.send(json.build());
		});
		if(config.getSettings().getToken().equalsIgnoreCase("TO_GENERATE")) {
			app.post("/core/generate/firstKey", (req, res) -> {
				String key = req.getFormQuery("key");
				String domain = req.getFormQuery("domain");
				this.config.getSettings().setToken(key);
				this.config.getSettings().setDomain(domain);
				this.config.saveSettings();
				this.config.getLog().log(Level.INFO, "CMW Host : " + domain +" linked to CMWL !");
				JsonBuilder json = new JsonBuilder()
						.append("CODE", 200);
				res.setStatus(Status._200);
				res.send(json.build());
			});
		}
		authRequest();
	}

	private void authRequest() {
		app.use((req, res) -> {
			String ip = req.getIp();
			if(this.config.getSettings().isEnableWhitelistedIps()) {
				if(!this.config.getSettings().getWhitelistedIps().contains(ip)) {
					this.config.getLog().severe("IP " + ip + " try to execute request, this IP is not in whitelist IPs !");
					JsonBuilder json = new JsonBuilder()
							.append("CODE", 401)
							.append("MESSAGE", "This IP " + ip + " is not allowed to execute requests !");
					res.setStatus(Status._401);
					res.send(json.build());
					return;
				}
			}
			//HEADER CHECK
			if(!config.getSettings().getToken().equalsIgnoreCase("TO_GENERATE")) {
				try {
					if(req.getHeader("X-CMW-ACCESS").size() == 0) {
						this.config.getLog().severe("Cancelled host " + req.getAddress().getHostName() + " request, there is no CMW header !");
						JsonBuilder json = new JsonBuilder()
								.append("CODE", 401)
								.append("MESSAGE", "No CMW header found !");
						res.setStatus(Status._401);
						res.send(json.build());
						return;
					}
					String key = req.getHeader("X-CMW-ACCESS").get(0).toString();
					//HOST CHECK
					if(!req.getAddress().getHostName().trim().equalsIgnoreCase(this.getConfig().getSettings().getDomain())) {
						this.config.getLog().severe("Cancelled host " + req.getAddress().getHostName() + " request, this host is not registered !");
						JsonBuilder json = new JsonBuilder()
								.append("CODE", 401)
								.append("MESSAGE", "This host in unknown !");
						res.setStatus(Status._401);
						res.send(json.build());
						return;
					}
					//KEY CHECK
					if(!key.trim().equals(this.config.getSettings().getToken())) {
						this.config.getLog().severe("Cancelled host " + req.getAddress().getHostName() + " request, invalid key !");
						JsonBuilder json = new JsonBuilder()
								.append("CODE", 401)
								.append("MESSAGE", "Cancelled host " + req.getAddress().getHostName() + " request, invalid key !");
						res.setStatus(Status._401);
						res.send(json.build());
						return;
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(config.getSettings().isLogRequests()) {
				this.config.getLog().log(Level.INFO, "Executed request by: " + req.getAddress().getHostName() + ", " + req.getPath());
			}
		});
	}

	public void startWebServer(int port) {
		this.app.listen(this.config.getSettings().getPort());
		try {
			URL whatismyip = new URL("https://ip.conceptngo.fr/myIP");
			URLConnection uc = whatismyip.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: " + config.getVersion());
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			JsonObject json = new Gson().fromJson(in.readLine(), JsonObject.class);
			String ip = json.get("IP").getAsString();
			this.config.getLog().info("External IP: " + ip);
			URL checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/" + (this.getConfig().getSettings().isBindToDefaultPort() ? "25565" : this.config.getSettings().getPort()));
			uc = checkURL.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: " + config.getVersion());
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			json = new Gson().fromJson(in.readLine(), JsonObject.class);
			boolean reachable = json.get("REACHABLE").getAsBoolean();
			if(reachable) {
				this.config.getLog().info("Port " + (this.getConfig().getSettings().isBindToDefaultPort() ? port : this.config.getSettings().getPort()) + " is properly forwarded and is externally accessible.");
			}
			else {
				this.config.getLog().severe("Port " + (this.getConfig().getSettings().isBindToDefaultPort() ? port : this.config.getSettings().getPort()) + " is not properly forwarded.");
			}
		} catch (Exception e) {
			this.config.getLog().severe("Cannot joint API to get IP and PORT verification, maybe API is down ");
		}
	}

	public void addRoute(CMWLPackage cmwlPackage, IRoute route){
		cmwlPackage.log(Level.INFO, "Register type " + route.getRouteType() + " route: /" + cmwlPackage.getRoutePrefix() + "/" + route.getRouteName());
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
			res.setStatus(Status._404);
			res.send(json.build());
		});
	}

	public void disable() {
		this.app.stop();
		if(injector != null) {
			this.injector.close();
		}
	}
}
