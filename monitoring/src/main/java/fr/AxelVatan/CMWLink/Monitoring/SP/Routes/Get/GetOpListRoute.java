package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;
import fr.AxelVatan.CMWLink.Monitoring.SP.ServerInfos.PlayerInfo;

public class GetOpListRoute  extends CMWLRoute<Main>{

	public GetOpListRoute(Main plugin) {
		super(plugin, "opList", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		JsonArray jObjects = new JsonArray();
		for(PlayerInfo pInfoClass : this.getPlugin().getServerInfos().getOpList()) {
			JsonObject pInfo = new JsonObject();
			pInfo.addProperty("USERNAME", pInfoClass.getUsername());
			pInfo.addProperty("UUID", pInfoClass.getUuid());
			jObjects.add(pInfo);
		}
		return new JsonBuilder("CODE", 200).append("OP_LIST", jObjects)
				.build();
	}

}
