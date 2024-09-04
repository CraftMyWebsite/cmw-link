package fr.CraftMyWebsite.CMWLink.Common.Utils.v1_19_R3;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import fr.CraftMyWebsite.CMWLink.Common.Utils.OfflinePlayerLoader;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.UUID;

public class OfflinePlayerLoader_v1_19_R3 extends OfflinePlayerLoader{

    public Player load(String exactPlayerName) {
        try {
            UUID uuid = matchUser(exactPlayerName);
            if (uuid == null) {
                return null;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            return loadFromOfflinePlayer(player);
        }
        catch (Exception | Error e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player loadFromOfflinePlayer(OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        GameProfile profile = new GameProfile(player.getUniqueId(), Optional.ofNullable(player.getName()).orElse(""));
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        EntityPlayer entity = new EntityPlayer(server, world, profile);
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
