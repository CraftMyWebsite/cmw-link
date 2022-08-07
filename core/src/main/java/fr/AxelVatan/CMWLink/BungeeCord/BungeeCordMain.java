package fr.AxelVatan.CMWLink.BungeeCord;

import fr.AxelVatan.CMWLink.Common.Config.ConfigFile;
import fr.AxelVatan.CMWLink.Common.Config.StartingFrom;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCordMain extends Plugin {
	
	private @Getter ConfigFile configFile;
	
    @Override
    public void onEnable() {
    	this.getLogger().info("==========================================");
    	this.configFile = new ConfigFile(StartingFrom.BUNGEECORD, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    	this.getLogger().info("==========================================");
    	this.getProxy().getPluginManager().registerCommand(this, new BG_Commands(this));
    }

    public void resetConfig() {
    	this.configFile = new ConfigFile(StartingFrom.BUNGEECORD, this.getDataFolder(), this.getLogger(), this.getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
    	this.configFile.getWebServer().disable();
    	this.configFile.getPackages().disablePackages();
    }
    
}
