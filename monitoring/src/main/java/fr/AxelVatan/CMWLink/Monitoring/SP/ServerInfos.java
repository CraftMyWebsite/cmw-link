package fr.AxelVatan.CMWLink.Monitoring.SP;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ServerInfos {

	private Main main;
	private Server server;
	
	public ServerInfos(Main main) {
		this.main = main;
		this.server = Bukkit.getServer();
	}
	
	public int getCurrentPlayers() {
		return this.server.getOnlinePlayers().size();
	}
	
	public int getMaxPlayers() {
		return this.server.getMaxPlayers();
	}
	
	public List<PlayerInfo> getPlayersLit(){
		List<PlayerInfo> playersList = new ArrayList<PlayerInfo>();
		for(Player player :  this.server.getOnlinePlayers()) {
			playersList.add(new PlayerInfo(player.getName(), player.getUniqueId().toString().replace("-", ""), player.getAddress().getHostString()));
		}
		return playersList;
	}
	
	public String getMotd() {
		return this.server.getMotd();
	}
	
	public String getVersion() {
		return this.server.getBukkitVersion();
	}
	
	public List<PlayerInfo> getOpList() {
		List<PlayerInfo> playersList = new ArrayList<PlayerInfo>();
		for(OfflinePlayer player : this.server.getOperators()) {
			playersList.add(new PlayerInfo(player.getName(), player.getUniqueId().toString().replace("-", ""), null));
		}
		return playersList;
	}
	
	public List<BanInfo> getBanList(Type banType) {
		List<BanInfo> banList = new ArrayList<BanInfo>();
		for(BanEntry entry : this.server.getBanList(banType).getBanEntries()) {
			banList.add(new BanInfo(entry.getReason(), entry.getSource(), entry.getTarget(), entry.getCreated(), entry.getExpiration()));
		}
		return banList;
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
	
	@AllArgsConstructor
	public class PlayerInfo {
		private @Getter String username;
		private @Getter String uuid;
		private @Getter String ip;
	}
	
	@AllArgsConstructor
	public class BanInfo {
		private @Getter String reason;
		private @Getter String source;
		private @Getter String target;
		private @Getter Date created;
		private @Getter Date expiration;
	}
}
