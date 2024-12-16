package fr.CraftMyWebsite.CMWLink.Shop.Common;

import fr.CraftMyWebsite.CMWLink.Common.Config.IConfigFile;
import fr.CraftMyWebsite.CMWLink.Common.Config.Persist;
import lombok.Getter;

import java.io.File;
import java.util.logging.Logger;

public class Config extends IConfigFile {
    private @Getter File filePath;
    private @Getter Logger log;
    private @Getter Settings settings;
    private Persist persist;

    public Config(File filePath, Logger log) {
        super(filePath, log);
        this.filePath = filePath;
        this.persist = new Persist(this);

        settings = persist.getFile(Config.Settings.class).exists() ? persist.load(Config.Settings.class) : new Config.Settings();
        saveSettings();
    }

    public void saveSettings() {
        if (settings != null) persist.save(settings);
    }

    public class Settings {
        private @Getter String prefix = "&8&l[&6Shop&8&l]&r ";
        private @Getter String rewardQueueText = "&7Vous venez de recevoir votre achat &l{item}";
        private @Getter String broadcastPurchase = "&aMerci &l{username}&r&a pour l'achat de &l{item}";

    }
}
