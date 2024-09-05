package fr.CraftMyWebsite.CMWLink.Votes.Common;

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
        private @Getter String prefix = "&8&l[&6Votes&8&l]&r ";
        private @Getter String sendVoteText = "&a&l{username} &7remporte &a&o{reward_name} &7pour avoir voté sur le site &6&n{site_name}&7.";
        private @Getter String rewardQueueText = "&7Vous avez reçu la &6&lrécompense &7de votre &a&lvote !";

    }
}
