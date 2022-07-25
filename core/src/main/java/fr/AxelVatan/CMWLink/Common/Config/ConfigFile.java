package fr.AxelVatan.CMWLink.Common.Config;

import java.io.File;
import java.util.logging.Logger;

import fr.AxelVatan.CMWLink.Common.Packages.Packages;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import lombok.Getter;

public class ConfigFile {

	private @Getter StartingFrom startingFrom;
	private @Getter File filePath;
	private @Getter Logger log;
	private @Getter String version;
	private @Getter Settings config;
	private @Getter Packages packages;
	private @Getter WebServer webServer;

	public ConfigFile(StartingFrom startingFrom,File filePath, Logger log, String version){
		this.startingFrom = startingFrom;
		this.filePath = filePath;
		this.log = log;
		this.version = version;
		log.info("Loading configuration...");
		Persist persist = new Persist(this);
		config = persist.getFile(Settings.class).exists() ? persist.load(Settings.class) : new Settings();
		if (config != null) persist.save(config);
		log.info("Configuration loaded successfully !");
		log.info("- Port: " + config.getPort());
		log.info("- Log Requests: " + config.isLogRequests());
		log.info("- Using proxy: " + config.isUseProxy());
		this.webServer = new WebServer(this);
		this.packages = new Packages(log, filePath, webServer);
		switch(this.startingFrom) {
		case BUNGEECORD:
			if(this.config.useProxy) {
				this.startWebServer();
			}else {
				log.severe("UseProxy on this BungeeCord Proxy is not set to true !");
				log.severe("CMW-Link will be useless");
			}
			break;
		case SPIGOT:
			if(this.config.isUseProxy()) {
				log.info("Waiting requests from the proxy");
			}else {
				this.startWebServer();
			}
			break;
		case VELOCITY:
			if(this.config.useProxy) {
				this.startWebServer();
			}else {
				log.severe("UseProxy on this Velocity Proxy is not set to true !");
				log.severe("CMW-Link will be useless");
			}
			break;
		}
	}

	private void startWebServer() {
		this.webServer.createRoutes();
		this.webServer.startWebServer();
	}

	public class Settings{

		private @Getter int port = 24102;
		private @Getter boolean logRequests = true;
		private @Getter boolean useProxy = false;
		private @Getter String username = "admin";
		private @Getter String password = "changeme";
		private @Getter String salt = "AN AWESOME SALT FOR HASH";

	}
}
