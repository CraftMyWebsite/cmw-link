package fr.CraftMyWebsite.CMWLink.Votes.SP.Hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VotesExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getName() {
        return "CMWLink - Votes";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cmwl";
    }

    @Override
    public @NotNull String getAuthor() {
        return "CraftMyWebsite / Teyir";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (params.equals("votes_total_votes")){
            return "a";
        }
        return null;
    }
}
