package fr.AxelVatan.CMWLink.Boutique.SP;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import fr.AxelVatan.CMWLink.Boutique.SP.Routes.TestGive;
import fr.AxelVatan.CMWLink.Boutique.SP.Routes.VersionRoute;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class Main extends CMWLPackage implements PluginMessageListener{

	private Server mcServer;

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique for Spigot enabled.");
		if(this.isUseProxy()) {
			this.mcServer = Bukkit.getServer();
			mcServer.getMessenger().registerOutgoingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"), "cmw:boutique");
			mcServer.getMessenger().registerIncomingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"), "cmw:boutique", this);
		}
	}

	@Override
	public void disable() {
		mcServer.getMessenger().unregisterOutgoingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"));
		mcServer.getMessenger().unregisterIncomingPluginChannel(mcServer.getPluginManager().getPlugin("CraftMyWebsite_Link"));
	}

	@Override
	public void registerRoutes() {
		if(!this.isUseProxy()) {
			this.addRoute(new VersionRoute(this));
			this.addRoute(new TestGive(this));
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("cmw:boutique")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("give")) {
			String username = in.readUTF();
			String item = in.readUTF();
			int qty = in.readInt();
			this.log(Level.INFO, "Received give order: " + username + ", " + item + ", " + qty);
		}
	}
}
