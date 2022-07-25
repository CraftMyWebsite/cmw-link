package fr.AxelVatan.CMWLink.Monitoring.SP.Routes.Get;

import java.util.HashMap;

import org.bukkit.BanList.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Monitoring.SP.Main;
import fr.AxelVatan.CMWLink.Monitoring.SP.ServerInfos.BanInfo;

public class GetBanListRoute extends CMWLRoute<Main>{

	public GetBanListRoute(Main plugin) {
		super(plugin, "banList", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		return new JsonBuilder("CODE", 200).append("BAN_LIST_USERNAMES", createJsonArray(Type.NAME))
				.append("BAN_LIST_IPS", createJsonArray(Type.IP))
				.build();
	}

	private JsonArray createJsonArray(Type banType) {
		JsonArray banList = new JsonArray();
		for(BanInfo banInfo : this.getPlugin().getServerInfos().getBanList(Type.NAME)) {
			JsonObject banInfoJs = new JsonObject();
			banInfoJs.addProperty("REASON", banInfo.getReason());
			banInfoJs.addProperty("SOURCE", banInfo.getSource());
			banInfoJs.addProperty("TARGET", banInfo.getTarget());
			banInfoJs.addProperty("CREATED", banInfo.getCreated().getTime());
			if(banInfo.getExpiration() != null) {
				banInfoJs.addProperty("EXPIRATION", banInfo.getExpiration().getTime());
			}else {
				banInfoJs.addProperty("EXPIRATION", "PERMANENT");
			}
			banList.add(banInfoJs);
		}
		return banList;
	}
}