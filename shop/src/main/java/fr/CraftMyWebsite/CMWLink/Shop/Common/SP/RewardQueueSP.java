package fr.CraftMyWebsite.CMWLink.Shop.Common.SP;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Shop.Common.Config;
import fr.CraftMyWebsite.CMWLink.Shop.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Shop.Common.RewardQueue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewardQueueSP extends RewardQueue {

    private Plugin plugin;

    public RewardQueueSP(CMWLPackage main, Config config) {
        super(main, config);
        this.plugin = main.getSpServer().getPluginManager().getPlugin("CraftMyWebsite_Link");
        main.getSpServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                proccessQueue();
            }
        }, 200, 200);
    }

    private void proccessQueue() {
        for (Player player : getMain().getSpServer().getOnlinePlayers()) {
            this.executeQueuePlayer(player, player.getUniqueId().toString().replace("-", "").toLowerCase());
        }
    }

    private void executeQueuePlayer(Player spPlayer, String uuid) {
        if (this.getQueue().containsKey(uuid)) {
            QueuedReward rewards = this.getQueue().get(uuid);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for (String cmd : rewards.getCmds()) {
                Runnable worker = new Runnable() {
                    @Override
                    public void run() {
                        getMain().getSpServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                getMain().getSpServer().dispatchCommand(getMain().getSpServer().getConsoleSender(), cmd);
                            }
                        });
                    }
                };
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            this.getQueue().remove(uuid);
            new File(getMain().getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
            spPlayer.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', getConfig().getSettings().getPrefix() + " " + getConfig().getSettings().getRewardQueueText()
                            .replace("{item}", rewards.getItem())
                    ));
        }
    }
}