package fr.CraftMyWebsite.CMWLink.Shop.BG;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Shop.BG.Routes.RewardCmd;
import fr.CraftMyWebsite.CMWLink.Shop.Common.BG.RewardQueueBG;
import fr.CraftMyWebsite.CMWLink.Shop.Common.Config;
import fr.CraftMyWebsite.CMWLink.Shop.Common.RewardQueue;
import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends CMWLPackage {

    private @Getter Config config;
    private @Getter RewardQueue queue;

    @Override
    public void enable() {
        this.log(Level.INFO, "Shop for BungeeCord enabled.");

        this.config = new Config(this.getMainFolder(), Logger.getAnonymousLogger());
        this.queue = new RewardQueueBG(this, config);
    }

    @Override
    public void disable() {

    }

    @Override
    public void registerRoutes() {
        this.addRoute(new RewardCmd(this));
    }

}
