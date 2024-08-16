package fr.AxelVatan.CMWLink.Common.Utils;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import lombok.Getter;

public class Utils {

	private Logger log;
	private StartingFrom startingFrom;
	
	private @Getter Versions version;
	private @Getter OfflinePlayerLoader offlinePlayerLoader;
	
	public Utils(Logger log, StartingFrom startingFrom) {
		this.log = log;
		this.startingFrom = startingFrom;
	}
	
	@SuppressWarnings("deprecation")
	public boolean init(boolean say) {
		switch(startingFrom) {
		case BUNGEECORD:
			return true;
		case SPIGOT:
			try {
				version = Versions.fromVersion(Bukkit.getVersion());
				if(version != null) {
					if(say) {
						log.info("Detected version of MC: " + version.toString());
					}
					offlinePlayerLoader = (OfflinePlayerLoader) Class.forName("fr.AxelVatan.CMWLink.Common.Utils." + version.getPackageName() +".OfflinePlayerLoader_" + version.getPackageName()).newInstance();
					
				}else {
					if(say) {
						log.severe("Unsupported Version Detected: " + Bukkit.getVersion());
						log.severe("Disabling CWML...");
					}
					Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("CraftMyWebsite_Link"));
				}
				return true;
			} catch (Exception e) {
				if(say) {
					log.severe("Unsupported Version Detected: " + Bukkit.getVersion());
					log.severe("Disabling CWML...");
				}
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
