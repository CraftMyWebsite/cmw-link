package fr.AxelVatan.CMWLink.Votes.SP.Hooks;

import fr.AxelVatan.CMWLink.Votes.SP.Main;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class HooksManager{
    private final Main main;

    public HooksManager(Main main) {
        this.main = main;

        registerPlaceholderAPI();
    }

    public void registerPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        if (new VotesExpansion().register()) {
            this.main.log(Level.INFO, "PlaceholerAPI hook with success !");
        }

    }
}
