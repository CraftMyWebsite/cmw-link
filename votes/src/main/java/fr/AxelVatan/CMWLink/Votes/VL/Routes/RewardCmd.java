package fr.AxelVatan.CMWLink.Votes.VL.Routes;

import java.util.Arrays;
import java.util.HashMap;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Votes.Common.QueuedReward;
import fr.AxelVatan.CMWLink.Votes.VL.Main;

public class RewardCmd extends CMWLRoute<Main>{

	public RewardCmd(Main main) {
		super(main, "send/reward/:username/:cmd", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String username = params.get("username");
		String cmd = params.get("cmd");
		QueuedReward qReward = new QueuedReward(username.toLowerCase(), Arrays.asList(cmd.split("\\|")));
		this.getPlugin().getQueue().addToQueue(qReward);
		return new JsonBuilder().append("CODE", 200).build();
	}
}
