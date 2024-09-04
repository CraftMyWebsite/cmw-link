package fr.CraftMyWebsite.CMWLink.Shop.SP.Routes;

import java.util.HashMap;

import fr.CraftMyWebsite.CMWLink.Shop.SP.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.CraftMyWebsite.CMWLink.Shop.Result;
import fr.CraftMyWebsite.CMWLink.Common.Config.JsonBuilder;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.CMWLRoute;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.RouteType;

public class TestGive extends CMWLRoute<Main>{

	public TestGive(Main plugin) {
		super(plugin, "give/:username/:item/:qty", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		String username = params.get("username");
		String item = params.get("item");
		int qty = Integer.valueOf(params.get("qty"));
		JsonBuilder json = new JsonBuilder();
		try {
			Material mat = Material.getMaterial(item);
			if(mat == null) {
				json.append("CODE", 404);
				json.append("MESSAGE", Result.ITEM_NOT_FOUND.name());
				return json.build();
			}else {
				Player player = Bukkit.getServer().getPlayer(username);
				if(player == null) {
					json.append("CODE", 404);
					json.append("MESSAGE", Result.PLAYER_NOT_FOUND.name());
					return json.build();
				}else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
					Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
					json.append("CODE", 200);
					json.append("MESSAGE", Result.SUCCESS.name());
					return json.build();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			json.append("CODE", 500);
			json.append("MESSAGE", Result.UNKNOWN_ERROR.name() + ", please check console for errors");
			return json.build();
		}
	}
}
