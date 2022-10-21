package fr.AxelVatan.CMWLink.Votes.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public class RewardQueue {

	private @Getter CMWLPackage main;
	private Persist persist = new Persist(this);
	private Config config;
	private HashMap<String, List<QueuedReward>> queue;
	
	public RewardQueue(CMWLPackage main, Config config) {
		this.main = main;
		this.persist = new Persist(this);
		this.config = config;
		this.queue = new HashMap<String, List<QueuedReward>>();
		loadQueuedRewards();
	}
	
	public void addToQueue(QueuedReward qReward) {
		if(this.queue.containsKey(qReward.getUuid())) {
			List<QueuedReward> qRewards = new ArrayList<QueuedReward>();
			qRewards.addAll(this.queue.get(qReward.getUuid()));
			qRewards.add(qReward);
			this.queue.put(qReward.getUuid(), qRewards);
		}else {
			this.queue.put(qReward.getUuid(), Arrays.asList(qReward));
		}
		File qFile = new File(main.getMainFolder() + File.separator + "Queue");
		if(!qFile.exists()) {
			qFile.mkdirs();
		}
		qFile = new File(main.getMainFolder() + File.separator + "Queue" + File.separator + qReward.getUuid() + ".json");
		persist.save(this.queue.get(qReward.getUuid()), qFile);
	}

	private void loadQueuedRewards() {
		
	}
	
	public void save() {
		
	}
}
