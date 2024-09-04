package fr.CraftMyWebsite.CMWLink.Shop.SP;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.CraftMyWebsite.CMWLink.Shop.Result;
import fr.CraftMyWebsite.CMWLink.Shop.SP.Routes.TestGive;
import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage implements PluginMessageListener{

	private Server mcServer;

	@Override
	public void enable() {
		this.log(Level.INFO, "Shop for Spigot enabled.");
		if(this.isUseProxy()) {
			this.mcServer = Bukkit.getServer();
			mcServer.getMessenger().registerOutgoingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"), "cmw:shop");
			mcServer.getMessenger().registerIncomingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"), "cmw:shop", this);
		}
	}

	@Override
	public void disable() {
	}

	@Override
	public void registerRoutes() {
		if(!this.isUseProxy()) {
			this.addRoute(new TestGive(this));
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("cmw:shop")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("give")) {
			String username = in.readUTF();
			String item = in.readUTF();
			int qty = in.readInt();
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(subchannel);
			try {
				Material mat = Material.getMaterial(item);
				if(mat == null) {
					out.writeUTF(Result.ITEM_NOT_FOUND.name());
				}else {
					Player playerReq = Bukkit.getServer().getPlayer(username);
					if(playerReq == null) {
						out.writeUTF(Result.PLAYER_NOT_FOUND.name());
					}else {
						ItemStack itemStack = new ItemStack(Material.getMaterial(item), qty);
						Bukkit.getServer().getPlayer(username).getInventory().addItem(itemStack);
						this.log(Level.INFO, "Received give order: " + username + ", " + item + ", " + qty);
						out.writeUTF(Result.SUCCESS.name());
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				out.writeUTF(Result.ITEM_NOT_FOUND.name());
			}
			Bukkit.getPlayer(username).sendPluginMessage(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"), "cmw:shop", out.toByteArray());
		}
	}
}
