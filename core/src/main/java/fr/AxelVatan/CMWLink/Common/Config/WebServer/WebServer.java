package fr.AxelVatan.CMWLink.Common.Config.WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import express.Express;
import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;

public class WebServer {

	private Express app;
	
	public WebServer(ConfigFile config) {
		app = new Express();
		app.all("/", (req, res) -> {
			res.send("WSH GROS, BIEN OU B1");
		});
		app.listen(config.getConfig().getPort());
		try {
			URL whatismyip = new URL("https://ip.conceptngo.fr/myIP");
			URLConnection uc = whatismyip.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: 1.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String ip = in.readLine();
			config.getLog().info("External IP: " + ip);
			URL checkURL = new URL("https://ip.conceptngo.fr/portOpen/" + ip + "/" + config.getConfig().getPort());
			uc = checkURL.openConnection();
			uc.setRequestProperty("User-Agent", "CraftMyWebsite-Link Version: 1.0");
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String code = in.readLine();
			if(code.equalsIgnoreCase("200")) {
				config.getLog().info("Port " + config.getConfig().getPort() + " is properly forwarded and is externally accessible.");
			}
			else {
				config.getLog().severe("Port " + config.getConfig().getPort() + " is not properly forwarded.");
			}
		} catch (Exception e) {
			config.getLog().severe("Cannot joint API to get IP and PORT verification: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
