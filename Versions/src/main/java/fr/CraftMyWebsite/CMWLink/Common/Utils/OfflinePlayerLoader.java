package fr.CraftMyWebsite.CMWLink.Common.Utils;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class OfflinePlayerLoader {

	public abstract Player load(String exactPlayerName);
	public abstract Player loadFromOfflinePlayer(OfflinePlayer player);
	public abstract UUID matchUser(String search);
}
