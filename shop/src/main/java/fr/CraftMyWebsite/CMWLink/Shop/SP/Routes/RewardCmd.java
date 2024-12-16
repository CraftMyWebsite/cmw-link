package fr.CraftMyWebsite.CMWLink.Shop.SP.Routes;

import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;
import fr.CraftMyWebsite.CMWLink.Shop.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Shop.SP.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class RewardCmd extends CMWLRoute<Main> {

    public RewardCmd(Main main) {
        super(main, "send/reward/:username/:cmd/:item", RouteType.GET);
    }

    @Override
    public String executeRoute(HashMap<String, String> params) {
        String username = params.get("username");
        String cmd = new String(Base64.getDecoder().decode(params.get("cmd")));
        String item = new String(Base64.getDecoder().decode(params.get("item")));

        String uuid = this.getPlugin().getUtils().getOfflinePlayerLoader().load(username).getUniqueId().toString().replace("-", "");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getConfig().getSettings().getPrefix() + this.getPlugin().getConfig().getSettings().getBroadcastPurchase()
                .replace("{username}", username)
                .replace("{item}", item)
        ));

        if (uuid != null) {
            QueuedReward qReward = new QueuedReward(uuid.toLowerCase(), item, Arrays.asList(cmd.split("\\|")));
            this.getPlugin().getQueue().addToQueue(qReward);
            return new JsonBuilder().append("CODE", 200).build();
        } else {
            return new JsonBuilder().append("CODE", 500).append("MESSAGE", "Player not found.").build();
        }
    }

}
