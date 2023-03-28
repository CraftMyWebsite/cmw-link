package fr.AxelVatan.CMWLink.Votes.Common.BG;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.Common.QueuedReward;
import fr.AxelVatan.CMWLink.Votes.Common.RewardQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RewardQueueBG extends RewardQueue{

	public RewardQueueBG(CMWLPackage main, Config config) {
		super(main, config);
		main.getBgServer().getScheduler().schedule(main.getBgServer().getPluginManager().getPlugin("CraftMyWebsite_Link"), new Runnable() {
			@Override
			public void run() {
				proccessQueue();
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	private void proccessQueue() {
		for(ProxiedPlayer player : getMain().getBgServer().getPlayers()) {
			this.executeQueuePlayer(player, player.getName().toLowerCase());
		}
	}

	private void executeQueuePlayer(ProxiedPlayer bgPlayer, String uuid) {
		if(getQueue().containsKey(uuid)) {
			QueuedReward rewards = this.getQueue().get(uuid);
			ExecutorService executor = Executors.newFixedThreadPool(5);
			for(String cmd : rewards.getCmds()) {
				Runnable worker = new Runnable() {
					@Override
					public void run() {
						getMain().getBgServer().getPluginManager().dispatchCommand(getMain().getBgServer().getConsole(), cmd);
					}
				};
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			getQueue().remove(uuid);
			new File(getMain().getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
			bgPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', getConfig().getPrefix() + " " + getConfig().getRewardQueueText())));
		}
	}
}
