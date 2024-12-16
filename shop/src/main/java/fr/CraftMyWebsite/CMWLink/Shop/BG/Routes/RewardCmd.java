package fr.CraftMyWebsite.CMWLink.Shop.BG.Routes;

import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;
import fr.CraftMyWebsite.CMWLink.Shop.BG.Main;
import fr.CraftMyWebsite.CMWLink.Shop.Common.QueuedReward;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

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
        String cmd = params.get("cmd");
        String item = new String(Base64.getDecoder().decode(params.get("item")));
        QueuedReward qReward = new QueuedReward(username.toLowerCase(), item, Arrays.asList(cmd.split("\\|")));
        this.getPlugin().getQueue().addToQueue(qReward);
        ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&', this.getPlugin().getConfig().getSettings().getPrefix() + this.getPlugin().getConfig().getSettings().getBroadcastPurchase()
                .replace("{username}", username)
                .replace("{item}", item))
        ));
        return new JsonBuilder().append("CODE", 200).build();
    }
}
