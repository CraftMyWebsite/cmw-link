package fr.AxelVatan.CMWLink.Common;

import java.io.File;
import java.util.logging.Logger;

import lombok.Getter;

public class ConfigFile {

	private @Getter File filePath;
	private @Getter Logger log;
	private @Getter Settings config;
	
	public ConfigFile(File filePath, Logger log){
		this.filePath = filePath;
		this.log = log;
		log.info("Loading configuration...");
		Persist persist = new Persist(this);
		config = persist.getFile(Settings.class).exists() ? persist.load(Settings.class) : new Settings();
		if (config != null) persist.save(config);
		log.info("Configuration loaded successfully !");
		log.info("Port: " + config.getPort());
	}

	private class Settings{
		private @Getter int port = 24102;
	}
}
