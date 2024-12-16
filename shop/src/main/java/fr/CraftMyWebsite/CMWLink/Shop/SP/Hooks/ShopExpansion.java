package fr.CraftMyWebsite.CMWLink.Shop.SP.Hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getName() {
        return "CMWLink - Shop";
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

        if (params.equals("shop_example")){
            return "example";
        }
        return null;
    }
}
