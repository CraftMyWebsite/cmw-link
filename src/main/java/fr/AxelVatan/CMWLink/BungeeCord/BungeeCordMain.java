package fr.AxelVatan.CMWLink.BungeeCord;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCordMain extends Plugin {
	
	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(this.getDataFolder(), this.getLogger());
    	this.getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
    	this.configFile.getPackages().disablePackages();
    }
    
}
