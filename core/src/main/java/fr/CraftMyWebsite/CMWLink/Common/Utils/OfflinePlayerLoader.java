package fr.CraftMyWebsite.CMWLink.Common.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerLoader {

	public OfflinePlayer load(String exactPlayerName) {
		if (exactPlayerName == null || exactPlayerName.isEmpty()) {
			return null;
		}
		UUID uuid = matchUser(exactPlayerName);
		if (uuid == null) {
			return null;
		}
		return Bukkit.getOfflinePlayer(uuid);
	}

	public OfflinePlayer loadFromOfflinePlayer(OfflinePlayer player) {
		return player;
	}

	public UUID matchUser(String search) {
		if (search == null || search.isEmpty()) {
			return null;
		}
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : offlinePlayers) {
			String name = player.getName();
			if (name == null) {
				continue;
			}
			if (name.equalsIgnoreCase(search)) {
				return player.getUniqueId();
			}
		}
		return null;
	}
}
