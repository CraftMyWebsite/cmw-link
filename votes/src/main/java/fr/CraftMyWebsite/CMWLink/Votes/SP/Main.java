package fr.CraftMyWebsite.CMWLink.Votes.SP;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import fr.CraftMyWebsite.CMWLink.Votes.Common.SP.RewardQueueSP;
import fr.CraftMyWebsite.CMWLink.Votes.SP.Routes.RewardCmd;
import fr.CraftMyWebsite.CMWLink.Votes.SP.Routes.VoteReceived;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class Main extends CMWLPackage {
    private Plugin plugin;
    private @Getter Config config;
    private @Getter RewardQueue queue;

    @Override
    public void enable() {
        this.plugin = this.getSpServer().getPluginManager().getPlugin("CraftMyWebsite_Link");

        this.log(Level.INFO, "Votes for Spigot enabled.");

        this.config = new Config(this.getMainFolder(), plugin.getLogger());
        this.queue = new RewardQueueSP(this, config);
    }

    @Override
    public void disable() {

    }

    @Override
    public void registerRoutes() {
        this.addRoute(new VoteReceived(this));
        this.addRoute(new RewardCmd(this));
    }
}