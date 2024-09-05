package fr.CraftMyWebsite.CMWLink.Votes.Common.VL;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import fr.CraftMyWebsite.CMWLink.Votes.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RewardQueueVL extends RewardQueue {

    public RewardQueueVL(CMWLPackage main, Config config, ProxyServer server, PluginContainer pluginContainer) {
        super(main, config);
        server.getScheduler().buildTask(pluginContainer, this::proccessQueue)
                .delay(10, TimeUnit.SECONDS)
                .repeat(10, TimeUnit.SECONDS)
                .schedule();
    }

    private void proccessQueue() {
        for (Player player : getMain().getVlServer().getAllPlayers()) {
            this.executeQueuePlayer(player, player.getGameProfile().getName().toLowerCase());
        }
    }

    private void executeQueuePlayer(Player vlPlayer, String uuid) {
        if (getQueue().containsKey(uuid)) {
            QueuedReward rewards = this.getQueue().get(uuid);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for (String cmd : rewards.getCmds()) {
                Runnable worker = new Runnable() {
                    @Override
                    public void run() {
                        getMain().getVlServer().getCommandManager().executeAsync(getMain().getVlServer().getConsoleCommandSource(), cmd);
                    }
                };
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            this.getQueue().remove(uuid);
            new File(getMain().getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
            vlPlayer.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(getConfig().getSettings().getPrefix() + " " + getConfig().getSettings().getRewardQueueText()));
        }
    }
}
