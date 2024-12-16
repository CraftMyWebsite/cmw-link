package fr.CraftMyWebsite.CMWLink.Shop.VL;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Shop.Common.Config;
import fr.CraftMyWebsite.CMWLink.Shop.Common.RewardQueue;
import fr.CraftMyWebsite.CMWLink.Shop.Common.VL.RewardQueueVL;
import fr.CraftMyWebsite.CMWLink.Shop.VL.Routes.RewardCmd;
import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends CMWLPackage {
    private @Getter Config config;
    private @Getter RewardQueue queue;

    @Override
    public void enable() {
        this.log(Level.INFO, "Shop for Velocity enabled.");

        this.getVlServer().getPluginManager().getPlugin("craftmywebsite-link").ifPresentOrElse(plugin -> {
            this.config = new Config(this.getMainFolder(), Logger.getAnonymousLogger());
            this.queue = new RewardQueueVL(this, config, this.getVlServer(), plugin);

        }, () -> this.log(Level.SEVERE, "Could not find CraftMyWebsite_Link VL plugin"));
    }

    @Override
    public void disable() {

    }

    @Override
    public void registerRoutes() {
        this.addRoute(new RewardCmd(this));
    }
}
