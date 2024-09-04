package fr.CraftMyWebsite.CMWLink.Votes.VL.Routes;

import java.util.Base64;
import java.util.HashMap;

import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;
import fr.CraftMyWebsite.CMWLink.Votes.VL.Main;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VoteReceived extends CMWLRoute<Main>{
	
	public VoteReceived(Main main) {
		super(main, "send/validate/:username/:site_name/:reward_name", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String username = params.get("username");
		String siteName =  params.get("site_name");
		String rewardName = params.get("reward_name");
		this.getPlugin().getVlServer().sendMessage(LegacyComponentSerializer.legacy('&').deserialize(this.getPlugin().getConfig().getPrefix() + this.getPlugin().getConfig().getSendVoteText()
				.replace("{username}", username)
				.replace("{site_name}", new String(Base64.getDecoder().decode(siteName)))
				.replace("{reward_name}", new String(Base64.getDecoder().decode(rewardName)))
				));
		return new JsonBuilder().append("CODE", 200).build();
	}
}
