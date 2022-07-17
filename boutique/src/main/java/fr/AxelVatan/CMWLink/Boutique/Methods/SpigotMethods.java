package fr.AxelVatan.CMWLink.Boutique.Methods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.AxelVatan.CMWLink.Boutique.Main;

public class SpigotMethods implements IBoutiqueMethods{

	public SpigotMethods(Main main) {
	}

	@Override
	public Result runGive(String username, String item, int qty) {
		try {
			Material mat = Material.getMaterial(item);
			if(mat == null) {
				return Result.ITEM_NOT_FOUND;
			}else {
				Player player = Bukkit.getServer().getPlayer(username);
				if(player == null) {
					return Result.PLAYER_NOT_FOUND;
				}else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
					Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
					return Result.SUCCESS;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return Result.UNKNOWN_ERROR;
		}
	}

}
