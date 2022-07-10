package fr.AxelVatan.CMWLink.Common.Config;

import java.io.File;
import java.util.logging.Logger;

import fr.AxelVatan.CMWLink.Common.Packages.Packages;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import lombok.Getter;

public class ConfigFile {

	private @Getter File filePath;
	private @Getter Logger log;
	private @Getter Settings config;
	private @Getter Packages packages;
	private @Getter WebServer webServer;

	public ConfigFile(File filePath, Logger log){
		this.filePath = filePath;
		this.log = log;
		log.info("Loading configuration...");
		Persist persist = new Persist(this);
		config = persist.getFile(Settings.class).exists() ? persist.load(Settings.class) : new Settings();
		if (config != null) persist.save(config);
		log.info("Configuration loaded successfully !");
		log.info("Port: " + config.getPort());
		this.webServer = new WebServer(this);
		this.packages = new Packages(log, filePath, webServer);
		this.webServer.createRoutes();
		this.webServer.startWebServer();
	}

	public class Settings{
		private @Getter int port = 24102;
		private @Getter boolean useProxy = false;
	}
}
