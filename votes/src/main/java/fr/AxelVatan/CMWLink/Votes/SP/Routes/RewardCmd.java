package fr.AxelVatan.CMWLink.Votes.SP.Routes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
		String uuid = this.getPlugin().getOffLoader().load(username).getUniqueId().toString().replace("-", "");
		if(uuid != null) {
			List<String> cmds = Arrays.asList(cmd.split("@@@"));
			QueuedReward qReward = new QueuedReward(uuid, cmds);
			this.getPlugin().getQueue().addToQueue(qReward);
			return new JsonBuilder().append("CODE", 200).build();
		}else {
			return new JsonBuilder().append("CODE", 500).append("MESSAGE", "Player not found.").build();
		}
	}

}
