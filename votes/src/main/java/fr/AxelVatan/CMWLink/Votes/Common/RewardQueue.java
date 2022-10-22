package fr.AxelVatan.CMWLink.Votes.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

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
		this.plugin = Bukkit.getPluginManager().getPlugin("CraftMyWebsite_Link");
		if(!qFile.exists()) {
			qFile.mkdirs();
		}
 		loadQueuedRewards();
 		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				proccessQueue();
			}
 		}, 200, 200);
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
	}

	private void proccessQueue() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			String uuid = player.getUniqueId().toString().replace("-", "");
			QueuedReward rewards = this.queue.get(uuid);
			ExecutorService executor = Executors.newFixedThreadPool(5);
			for(String cmd : rewards.getCmds()) {
				Runnable worker = new Runnable() {
					@Override
					public void run() {
						Bukkit.getScheduler().runTask(plugin, new Runnable() {
							@Override
							public void run() {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
								player.sendMessage("Command: " + cmd + ", executed !");
							}
						});
					}
				};
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			this.queue.remove(uuid);
			new File(main.getMainFolder() + File.separator + "Queue" + File.separator + uuid + ".json").delete();
		}
	}
}	
