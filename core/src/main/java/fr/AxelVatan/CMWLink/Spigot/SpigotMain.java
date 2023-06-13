package fr.AxelVatan.CMWLink.Spigot;

import org.bukkit.plugin.java.JavaPlugin;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Utils.StartingFrom;
import lombok.Getter;

public class SpigotMain extends JavaPlugin{

	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(this.getServer(), StartingFrom.SPIGOT, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    	this.getLogger().info("==========================================");
    	if(this.configFile.getUtils().init(false)) {
        	this.getCommand("cmwl").setExecutor(new SP_Commands(this));
            this.getCommand("cmwl").setTabCompleter(new SP_Commands(this));
    	}
    }

    public void resetConfig() {
    	this.configFile = new ConfigFile(this.getServer(), StartingFrom.SPIGOT, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
    	if(this.configFile != null) {
    		this.configFile.getWebServer().disable();
        	this.configFile.getPackages().disablePackages();
    	}
    }
	
}
