package fr.AxelVatan.CMWLink.Votes.Common;

import lombok.Getter;

public class Config{

	private @Getter String prefix = "&8&l[&6Votes&8&l]&r ";
	private @Getter String sendVoteText = "&a&l{username} &7remporte &a&o{reward_name} &7pour avoir voté sur le site &6&n{site_name}&7.";
	//private @Getter String rewardQueueAnnounce = "&7Une récompense de vote est en attente, elle vous sera donnée dans quelques instant.";
	private @Getter String rewardQueueText = "&7Vous avez reçu la &6&lrécompense &7de votre &a&lvote !";
	
}
