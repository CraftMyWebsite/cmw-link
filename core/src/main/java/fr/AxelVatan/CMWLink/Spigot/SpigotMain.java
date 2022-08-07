package fr.AxelVatan.CMWLink.Spigot;

import org.bukkit.plugin.java.JavaPlugin;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Config.StartingFrom;
import lombok.Getter;

public class SpigotMain extends JavaPlugin{

	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(StartingFrom.SPIGOT, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    	this.getLogger().info("==========================================");
    	this.getCommand("cmwl").setExecutor(new SP_Commands(this));
    	this.getCommand("cmwl").setTabCompleter(new SP_Commands(this));
    }

    public void resetConfig() {
    	this.configFile = new ConfigFile(StartingFrom.SPIGOT, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
    	this.configFile.getWebServer().disable();
    	this.configFile.getPackages().disablePackages();
    }
	
}
