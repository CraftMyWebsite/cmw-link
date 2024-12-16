package fr.CraftMyWebsite.CMWLink.Shop.VL.Routes;

import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;
import fr.CraftMyWebsite.CMWLink.Shop.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Shop.VL.Main;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
        this.getPlugin().getVlServer().sendMessage(LegacyComponentSerializer.legacy('&').deserialize(this.getPlugin().getConfig().getSettings().getPrefix() + this.getPlugin().getConfig().getSettings().getBroadcastPurchase()
                .replace("{username}", username)
                .replace("{item}", item)
        ));
        return new JsonBuilder().append("CODE", 200).build();
    }
}
