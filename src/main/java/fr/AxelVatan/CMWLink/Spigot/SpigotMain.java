package fr.AxelVatan.CMWLink.Spigot;

import org.bukkit.plugin.java.JavaPlugin;

import fr.AxelVatan.CMWLink.Common.ConfigFile;
import lombok.Getter;

public class SpigotMain extends JavaPlugin{

	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(this.getDataFolder(), this.getLogger());
    	this.getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
    	
    }
	
}
