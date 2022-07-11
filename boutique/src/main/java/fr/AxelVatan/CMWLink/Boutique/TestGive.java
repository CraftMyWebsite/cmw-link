package fr.AxelVatan.CMWLink.Boutique;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class TestGive extends CMWLRoute<Main>{

	public TestGive(Main plugin) {
		super(plugin, "give/:username/:item/:qty", RouteType.GET);
	}

	//CODE GROSSIER JUSTE POUR TEST LES ROUTES AVEC PARAMS
	@Override
	public void execute(Request req, Response res) {
		try {
			String username = req.getParam("username");
			String item = req.getParam("item");
			int qty = Integer.valueOf(req.getParam("qty"));
			Material mat = Material.getMaterial(item);
			if(mat == null) {
				res.send("Item not found");
			}else {
				Player player = Bukkit.getServer().getPlayer(username);
				if(player == null) {
					res.send("Player not found");
				}else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
					Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
					res.send("Gived to " + username + " " + qty + " of " + item);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			res.send(e.getMessage());
		}
	}
}
