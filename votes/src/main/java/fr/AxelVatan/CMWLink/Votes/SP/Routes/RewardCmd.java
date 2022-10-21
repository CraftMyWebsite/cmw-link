package fr.AxelVatan.CMWLink.Votes.SP.Routes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Votes.Common.QueuedReward;
import fr.AxelVatan.CMWLink.Votes.SP.Main;

public class RewardCmd extends CMWLRoute<Main>{

	public RewardCmd(Main main) {
		super(main, "send/reward/:username/:cmd", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String username = params.get("username");
		String cmd = params.get("cmd");
		//TODO CLEAN AND SECURE
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(username);
		if(offPlayer != null) {
			QueuedReward qReward = new QueuedReward(offPlayer.getUniqueId().toString().replaceAll("-", ""), cmd);
			this.getPlugin().getQueue().addToQueue(qReward);
			return new JsonBuilder().append("CODE", 200).build();
		}else {
			return new JsonBuilder().append("CODE", 500).append("MESSAGE", "Player not found.").build();
		}
	}

}
