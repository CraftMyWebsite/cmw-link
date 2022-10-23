package fr.AxelVatan.CMWLink.Common.Utils;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import lombok.Getter;

public class Utils {

	private Logger log;
	private StartingFrom startingFrom;
	
	private @Getter Versions mcVersion;
	private @Getter OfflinePlayerLoader offlinePlayerLoader;
	
	public Utils(Logger log, StartingFrom startingFrom) {
		this.log = log;
		this.startingFrom = startingFrom;
		
	}
	
	public boolean init() {
		switch(startingFrom) {
		case BUNGEECORD:
			return true;
		case SPIGOT:
			try {
				String bukkitVer = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
				mcVersion = Versions.valueOf(bukkitVer);
				log.info("Detected version of MC: " + mcVersion.toString());
				offlinePlayerLoader = (OfflinePlayerLoader) Class.forName("fr.AxelVatan.CMWLink.Common.Utils." + bukkitVer +".OfflinePlayerLoader_" + bukkitVer).newInstance();
				return true;
			} catch (Exception e) {
				log.severe("Unsupported Version Detected: " + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
				log.severe("Disabling CMWL_Votes...");
				Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("CraftMyWebsite_Link"));
				return false;
			}
		case VELOCITY:
			return true;
		default:
			return false;
		}
	}
}
