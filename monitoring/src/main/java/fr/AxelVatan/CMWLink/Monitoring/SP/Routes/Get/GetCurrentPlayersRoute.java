package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;
import fr.AxelVatan.CMWLink.Monitoring.SP.ServerInfos.PlayerInfo;

public class GetCurrentPlayersRoute extends CMWLRoute<Main>{

	public GetCurrentPlayersRoute(Main plugin) {
		super(plugin, "currentPlayers", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		JsonArray jObjects = new JsonArray();
		for(PlayerInfo pInfoClass : this.getPlugin().getServerInfos().getPlayersLit()) {
			JsonObject pInfo = new JsonObject();
			pInfo.addProperty("USERNAME", pInfoClass.getUsername());
			pInfo.addProperty("UUID", pInfoClass.getUuid());
			pInfo.addProperty("IP", pInfoClass.getIp());
			jObjects.add(pInfo);
		}
		return new JsonBuilder("CODE", 200)
				.append("CURRENT_PLAYERS_COUNT", this.getPlugin().getServerInfos().getCurrentPlayers())
				.append("PLAYERS_LIST", jObjects).build();
	}

}
