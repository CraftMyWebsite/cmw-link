package fr.CraftMyWebsite.CMWLink.Common.Utils.v1_16_R3;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import fr.CraftMyWebsite.CMWLink.Common.Utils.OfflinePlayerLoader;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;

public class OfflinePlayerLoader_v1_16_R3 extends OfflinePlayerLoader{
	
	public Player load(String exactPlayerName) {
		try {
			UUID uuid = matchUser(exactPlayerName);
			if (uuid == null) {
				return null;
			}
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			return loadFromOfflinePlayer(player);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		catch (Error e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Player loadFromOfflinePlayer(OfflinePlayer player) {
		if (player == null) {
			return null;
		}
		GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		EntityPlayer entity = new EntityPlayer(server, server.getWorlds().iterator().next(), profile, new PlayerInteractManager(server.getWorlds().iterator().next()));
		Player target = entity.getBukkitEntity();
		if (target != null) {
			target.loadData();
			return target;
		}
		return null;
	}

	public UUID matchUser(String search) {
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
