package fr.AxelVatan.CMWLink.Monitoring.SP;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class ServerInfos {

	private Main main;
	private Server server;
	
	public ServerInfos(Main main) {
		this.main = main;
		this.server = Bukkit.getServer();
	}
	
	public int getCurrentPlayers() {
		return this.server.getOfflinePlayers().length;
	}
	
	public int getMaxPlayers() {
		return this.server.getMaxPlayers();
	}
	
	public String getMotd() {
		return this.server.getMotd();
	}
	
	public String getVersion() {
		return this.server.getBukkitVersion();
	}
	
	public HashMap<Object, Object> getAllSettings(){
		HashMap<Object, Object> settings = new HashMap<Object, Object>();
		settings.put("ALLOW_NETHER", this.server.getAllowNether());
		settings.put("LEVEL_NAME", this.server.getWorlds().get(0).getName());
		settings.put("FLIGH_ALLOWED", this.server.getAllowFlight());
		settings.put("SERVER_PORT", this.server.getPort());
		settings.put("SERVER_IP", this.server.getIp());
		settings.put("MAX_WORLD_SIZE", this.server.getMaxWorldSize());
		settings.put("LEVEL_TYPE", this.server.getWorldType());
		settings.put("LEVEL_SEED", this.server.getWorlds().get(0).getSeed());
		settings.put("MAX_BUILD_HEIGHT", this.server.getWorlds().get(0).getMaxHeight());
		return settings;
	}
}
