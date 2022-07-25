package fr.AxelVatan.CMWLink.Boutique.SP.Routes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.Result;
import fr.AxelVatan.CMWLink.Boutique.SP.Main;
import fr.AxelVatan.CMWLink.Common.Config.JsonBuilder;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class TestGive extends CMWLRoute<Main>{

	public TestGive(Main plugin) {
		super(plugin, "give/:username/:item/:qty", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		String username = req.getParam("username");
		String item = req.getParam("item");
		int qty = Integer.valueOf(req.getParam("qty"));
		JsonBuilder json = new JsonBuilder();
		try {
			Material mat = Material.getMaterial(item);
			if(mat == null) {
				json.append("CODE", 404);
				json.append("MESSAGE", Result.ITEM_NOT_FOUND.name());
				res.send(json.build());
			}else {
				Player player = Bukkit.getServer().getPlayer(username);
				if(player == null) {
					json.append("CODE", 404);
					json.append("MESSAGE", Result.PLAYER_NOT_FOUND.name());
					res.send(json.build());
				}else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
					Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
					json.append("CODE", 200);
					json.append("MESSAGE", Result.SUCCESS.name());
					res.send(json.build());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			json.append("CODE", 500);
			json.append("MESSAGE", Result.UNKNOWN_ERROR.name() + ", please check console for errors");
			res.send(json.build());
		}
	}
}
