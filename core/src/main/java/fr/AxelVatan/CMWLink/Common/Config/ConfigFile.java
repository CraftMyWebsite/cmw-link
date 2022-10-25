package fr.AxelVatan.CMWLink.Common.Config;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;

import fr.AxelVatan.CMWLink.Common.Packages.Packages;
import fr.AxelVatan.CMWLink.Common.Utils.StartingFrom;
import fr.AxelVatan.CMWLink.Common.Utils.Utils;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import lombok.Getter;

public class ConfigFile {

	//SPIGOT
	private @Getter Server spServer;
	//BUNGEECORD
	private @Getter net.md_5.bungee.api.ProxyServer bgServer;
	//VELOCITY
	private @Getter com.velocitypowered.api.proxy.ProxyServer vlServer;
	
	private @Getter StartingFrom startingFrom;
	private @Getter File filePath;
	private @Getter Logger log;
	private @Getter String version;
	private @Getter Settings settings;
	private @Getter Utils utils;
	private @Getter Packages packages;
	private @Getter WebServer webServer;

	//			.~~~~.
	//			i====i_
	//			|cccc|_)
	//			|cccc|   GIVE ME A BEER ! <3
	//			`-==-'

	public ConfigFile(Server server, StartingFrom startingFrom, File filePath, Logger log, String version) {
		this.spServer = server;
		load(startingFrom, filePath, log, version);
	}
	
	public ConfigFile(net.md_5.bungee.api.ProxyServer server, StartingFrom startingFrom, File filePath, Logger log, String version) {
		this.bgServer = server;
		load(startingFrom, filePath, log, version);
	}
	
	public ConfigFile(com.velocitypowered.api.proxy.ProxyServer server, StartingFrom startingFrom, File filePath, Logger log, String version) {
		this.vlServer = server;
		load(startingFrom, filePath, log, version);
	}
	
	public void load(StartingFrom startingFrom, File filePath, Logger log, String version){
		this.startingFrom = startingFrom;
		this.filePath = filePath;
		this.log = log;
		this.version = version;
		this.utils = new Utils(log, startingFrom);
		if(this.utils.init()) {
			log.info("Loading configuration...");
			Persist persist = new Persist(this);
			settings = persist.getFile(Settings.class).exists() ? persist.load(Settings.class) : new Settings();
			if (settings != null) persist.save(settings);
			log.info("Configuration loaded successfully !");
			if(settings.isBindToDefaultPort()) {
				log.info("- WebServer binded to default server port");
			}else {
				log.info("- Port: " + settings.getPort());
			}
			log.info("- Log Requests: " + settings.isLogRequests());
			log.info("- Using proxy: " + settings.isUseProxy());
			log.info("- Load Uncertified packages: " + settings.isLoadUncertifiedPackages());
			if(settings.isEnableWhitelistedIps()) {
				log.info("- Whitelisted IP: " + settings.getWhitelistedIps());
			}
			this.webServer = new WebServer(this);
			switch(this.startingFrom) {
			case BUNGEECORD:
				this.packages = new Packages(bgServer, log, filePath, webServer, utils);
				if(this.settings.useProxy) {
					this.startWebServer();
				}else {
					log.severe("UseProxy on this BungeeCord Proxy is not set to true !");
					log.severe("CMW-Link will be useless");
				}
				break;
			case SPIGOT:
				this.packages = new Packages(spServer, log, filePath, webServer, utils);
				if(this.settings.isUseProxy()) {
					log.info("Waiting requests from the proxy");
				}else {
					this.startWebServer();
				}
				break;
			case VELOCITY:
				this.packages = new Packages(vlServer, log, filePath, webServer, utils);
				if(this.settings.useProxy) {
					this.startWebServer();
				}else {
					log.severe("UseProxy on this Velocity Proxy is not set to true !");
					log.severe("CMW-Link will be useless");
				}
				break;
			}
		}
	}

	private void startWebServer() {
		this.webServer.createRoutes();
		this.webServer.startWebServer();
	}

	public class Settings{

		private @Getter int port = 24102;
		private @Getter boolean bindToDefaultPort = false;
		private @Getter boolean loadUncertifiedPackages = false;
		private @Getter boolean logRequests = true;
		private @Getter boolean useProxy = false;
		private @Getter boolean enableWhitelistedIps = false;
		private @Getter List<String> whitelistedIps = Arrays.asList("127.0.0.1");
		private @Getter String token = "TOKEN FROM THE CMS";

	}
}
