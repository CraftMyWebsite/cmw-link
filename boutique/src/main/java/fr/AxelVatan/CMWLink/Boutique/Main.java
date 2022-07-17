package fr.AxelVatan.CMWLink.Boutique;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import fr.AxelVatan.CMWLink.Boutique.Methods.BungeeMethods;
import fr.AxelVatan.CMWLink.Boutique.Methods.IBoutiqueMethods;
import fr.AxelVatan.CMWLink.Boutique.Methods.SpigotMethods;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

public class Main extends CMWLPackage{

	private @Getter IBoutiqueMethods methods;

	@Override
	public void enable() {
		this.log(Level.INFO, "Boutique enabled.");
		//BG PART
		if(this.isInBungee()) {
			ProxyServer.getInstance().registerChannel("cmw-boutique");
			methods = new BungeeMethods(this);
		}
		//SPIGOT PART
		if(this.isUseProxyConfig()) {
			MessageSpigot msgSpigot = new MessageSpigot(this);
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Bukkit.getPluginManager().getPlugin("CraftMyWebsite-Link"), "cmw-boutique");
			Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Bukkit.getPluginManager().getPlugin("CraftMyWebsite-Link"), "cmw-boutique", msgSpigot);
			methods = new SpigotMethods(this); 
		}
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VersionRoute(this));
		this.addRoute(new TestGive(this));
	}
}
