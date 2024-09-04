package fr.CraftMyWebsite.CMWLink.Votes.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public class RewardQueue {

	private @Getter CMWLPackage main;
	private QueuePersist persist = new QueuePersist(this);
	private @Getter Config config;
	private @Getter HashMap<String, QueuedReward> queue;
	private File qFile;
	private @Getter Plugin plugin;

	public RewardQueue(CMWLPackage main, Config config) {
		this.main = main;
		this.persist = new QueuePersist(this);
		this.config = config;
		this.queue = new HashMap<String, QueuedReward>();
		this.qFile = new File(main.getMainFolder() + File.separator + "Queue");
		if(!qFile.exists()) {
			qFile.mkdirs();
		}
 		loadQueuedRewards();
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
}	
