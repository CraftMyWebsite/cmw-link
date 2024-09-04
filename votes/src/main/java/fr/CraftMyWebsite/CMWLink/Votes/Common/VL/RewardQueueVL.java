package fr.CraftMyWebsite.CMWLink.Votes.Common.VL;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.proxy.Player;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import fr.CraftMyWebsite.CMWLink.Votes.Common.QueuedReward;
import fr.CraftMyWebsite.CMWLink.Votes.Common.RewardQueue;
import fr.CraftMyWebsite.CMWLink.Votes.Common.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class RewardQueueVL extends RewardQueue {

	public RewardQueueVL(CMWLPackage main, Config config) {
		super(main, config);
		main.getVlServer().getScheduler().buildTask(main.getVlServer().getPluginManager().getPlugin("CraftMyWebsite_Link"), new Runnable() {
			@Override
			public void run() {
				proccessQueue();
			}
		}).delay(10, TimeUnit.SECONDS).repeat(10, TimeUnit.SECONDS).schedule();
	}

	private void proccessQueue() {
		for(Player player :  getMain().getVlServer().getAllPlayers()) {
			this.executeQueuePlayer(player, player.getGameProfile().getName().toLowerCase());
		}
	}
	
	private void executeQueuePlayer(Player vlPlayer, String uuid) {
		if(getQueue().containsKey(uuid)) {
			QueuedReward rewards = this.getQueue().get(uuid);
			ExecutorService executor = Executors.newFixedThreadPool(5);
			for(String cmd : rewards.getCmds()) {
				Runnable worker = new Runnable() {
					@Override
					public void run() {
						getMain().getVlServer().getCommandManager().executeAsync(getMain().getVlServer().getConsoleCommandSource(), cmd);
					}
				};
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			this.getQueue().remove(uuid);
			new File(getMain().getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
			vlPlayer.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(getConfig().getPrefix() + " " + getConfig().getRewardQueueText()));
		}
	}
}
