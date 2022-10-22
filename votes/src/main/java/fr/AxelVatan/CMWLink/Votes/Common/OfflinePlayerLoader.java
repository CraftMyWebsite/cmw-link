package fr.AxelVatan.CMWLink.Votes.Common;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;

public class OfflinePlayerLoader{
	
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
		EntityPlayer entity = new EntityPlayer(server, server.E().iterator().next(), profile, null);
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
