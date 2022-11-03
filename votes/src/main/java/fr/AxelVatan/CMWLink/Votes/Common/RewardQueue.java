package fr.AxelVatan.CMWLink.Votes.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RewardQueue {

	private @Getter CMWLPackage main;
	private Persist persist = new Persist(this);
	private Config config;
	private @Getter HashMap<String, QueuedReward> queue;
	private File qFile;
	private @Getter Plugin plugin;

	public RewardQueue(CMWLPackage main, Config config) {
		this.main = main;
		this.persist = new Persist(this);
		this.config = config;
		this.queue = new HashMap<String, QueuedReward>();
		this.qFile = new File(main.getMainFolder() + File.separator + "Queue");
		if(!qFile.exists()) {
			qFile.mkdirs();
		}
 		loadQueuedRewards();
 		switch(main.getStartingFrom()) {
		case BUNGEECORD:
			main.getBgServer().getScheduler().schedule(main.getBgServer().getPluginManager().getPlugin("CraftMyWebsite_Link"), new Runnable() {
				@Override
				public void run() {
					proccessQueue();
				}
			}, 10, 10, TimeUnit.SECONDS);
			break;
		case SPIGOT:
			this.plugin = main.getSpServer().getPluginManager().getPlugin("CraftMyWebsite_Link");
			main.getSpServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					proccessQueue();
				}
	 		}, 200, 200);
			break;
		case VELOCITY:
			main.getVlServer().getScheduler().buildTask(main.getVlServer().getPluginManager().getPlugin("CraftMyWebsite_Link"), new Runnable() {
				@Override
				public void run() {
					proccessQueue();
				}
			}).delay(10, TimeUnit.SECONDS).repeat(10, TimeUnit.SECONDS).schedule();
			break;
 		}
	}

	public void addToQueue(QueuedReward qReward) {
		if(this.queue.containsKey(qReward.getUuid())) {
			List<String> cmds = new ArrayList<>(this.queue.get(qReward.getUuid()).getCmds());
			cmds.addAll(qReward.getCmds());
			qReward.setCmds(cmds);
			this.queue.put(qReward.getUuid(), qReward);
		}else {
			this.queue.put(qReward.getUuid(), qReward);
		}
		qFile = new File(main.getMainFolder() + File.separator + "Queue" + File.separator + qReward.getUuid() + ".json");
		persist.save(this.queue.get(qReward.getUuid()), qFile);
	}

	private void loadQueuedRewards() {
		for (File fileEntry : new File(main.getMainFolder() + File.separator + "Queue").listFiles()) {
			QueuedReward reward = persist.load(QueuedReward.class, fileEntry);
			this.queue.put(fileEntry.getName().replace(".json", ""), reward);
	    }
		this.main.log(Level.INFO, "Loaded Reward Queue, there is " + this.queue.size() + " reward waiting !");
	}

	private void proccessQueue() {
		switch(main.getStartingFrom()) {
		case BUNGEECORD:
			for(ProxiedPlayer player : main.getBgServer().getPlayers()) {
				this.executeQueuePlayer(null, player, null, player.getName().toLowerCase());
			}
			break;
		case SPIGOT:
			for(Player player : main.getSpServer().getOnlinePlayers()) {
				this.executeQueuePlayer(player, null, null, player.getUniqueId().toString().replace("-", "").toLowerCase());
			}
			break;
		case VELOCITY:
			for(com.velocitypowered.api.proxy.Player player : main.getVlServer().getAllPlayers()) {
				this.executeQueuePlayer(null, null, player, player.getGameProfile().getName().toLowerCase());
			}
			break;
		}
	}
	
	private void executeQueuePlayer(Player spPlayer, ProxiedPlayer bgPlayer, com.velocitypowered.api.proxy.Player vlPlayer, String uuid) {
		if(this.queue.containsKey(uuid)) {
			QueuedReward rewards = this.queue.get(uuid);
			ExecutorService executor = Executors.newFixedThreadPool(5);
			for(String cmd : rewards.getCmds()) {
				Runnable worker = new Runnable() {
					@Override
					public void run() {
						switch(main.getStartingFrom()) {
						case BUNGEECORD:
							main.getBgServer().getPluginManager().dispatchCommand(main.getBgServer().getConsole(), cmd);
							break;
						case SPIGOT:
							main.getSpServer().getScheduler().runTask(plugin, new Runnable() {
								@Override
								public void run() {
									main.getSpServer().dispatchCommand(main.getSpServer().getConsoleSender(), cmd);
								}
							});
							break;
						case VELOCITY:
							main.getVlServer().getCommandManager().executeAsync(main.getVlServer().getConsoleCommandSource(), cmd);
							break;
						}
					}
				};
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			this.queue.remove(uuid);
			new File(main.getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
			switch(main.getStartingFrom()) {
			case BUNGEECORD:
				bgPlayer.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getPrefix() + " " + config.getRewardQueueText())));
				break;
			case SPIGOT:
				spPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPrefix() + " " + config.getRewardQueueText()));
				break;
			case VELOCITY:
				//vlPlayer.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getPrefix() + " " + config.getRewardQueueText()));
				break;
			}
		}
	}
}	
