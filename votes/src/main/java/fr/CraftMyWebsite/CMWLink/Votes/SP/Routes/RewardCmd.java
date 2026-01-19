package fr.CraftMyWebsite.CMWLink.Votes.SP.Routes;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;
import fr.CraftMyWebsite.CMWLink.Votes.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Votes.SP.Main;

public class RewardCmd extends CMWLRoute<Main>{

	public RewardCmd(Main main) {
		super(main, "send/reward/:username/:cmd", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String username = params.get("username");
		String cmd = new String(Base64.getDecoder().decode(params.get("cmd")));

		// Load the player (works for both online and offline players)
		org.bukkit.OfflinePlayer offlinePlayer = this.getPlugin().getUtils().getOfflinePlayerLoader().load(username);

		if (offlinePlayer == null) {
			this.getPlugin().log(java.util.logging.Level.WARNING, "Player not found: " + username + " (Vote reward)");
			return new JsonBuilder().append("CODE", 404).append("MESSAGE", "Player not found: " + username).build();
		}

		String uuid = offlinePlayer.getUniqueId().toString().replace("-", "");

		QueuedReward qReward = new QueuedReward(uuid.toLowerCase(), Arrays.asList(cmd.split("\\|")));
		this.getPlugin().getQueue().addToQueue(qReward);
		return new JsonBuilder().append("CODE", 200).build();
	}

}
