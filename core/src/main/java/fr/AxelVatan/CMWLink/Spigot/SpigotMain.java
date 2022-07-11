package fr.AxelVatan.CMWLink.Spigot;

import org.bukkit.plugin.java.JavaPlugin;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;

public class SpigotMain extends JavaPlugin{

	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    	this.getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
    	this.configFile.getWebServer().disable();
    	this.configFile.getPackages().disablePackages();
    }
	
}
