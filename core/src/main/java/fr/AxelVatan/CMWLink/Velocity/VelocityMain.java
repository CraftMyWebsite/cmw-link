package fr.AxelVatan.CMWLink.Velocity;

import java.nio.file.Path;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;

@Plugin(id = "craftmywebsite-link", name = "CraftMyWebsite-Link", version = "1.0", url = "https://craftmywebsite.fr/", description = "CraftMyWebsite-Link a java plugin for MC servers", authors = {"AxelVatan"})
public class VelocityMain {

	private @Getter ConfigFile configFile;

	@Inject
	public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		logger.info("==========================================");
		this.configFile = new ConfigFile(dataDirectory.toFile(), logger);
		logger.info("==========================================");
	}

	@Subscribe
	public void onPlayerChat(ProxyShutdownEvent event) {
		this.configFile.getPackages().disablePackages();
	}
}
