package fr.AxelVatan.CMWLink.Boutique.SP.Routes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.Result;
import fr.AxelVatan.CMWLink.Boutique.SP.Main;
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
		try {
			Material mat = Material.getMaterial(item);
			if(mat == null) {
				res.send(Result.ITEM_NOT_FOUND.name());
			}else {
				Player player = Bukkit.getServer().getPlayer(username);
				if(player == null) {
					res.send(Result.PLAYER_NOT_FOUND.name());
				}else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
					Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
					res.send(Result.SUCCESS.name());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			res.send(Result.UNKNOWN_ERROR.name());
		}
	}
}
