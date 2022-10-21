package fr.AxelVatan.CMWLink.Votes.Common;

import fr.AxelVatan.CMWLink.Common.Packages.PackageConfig;
import lombok.Getter;

public class Config extends PackageConfig{

	private @Getter String sendVoteText = "&a&l{username} &7à gagné &a&o{reward_name} &7pour avoir voté sur le site &6&n{site_name}&7.";
	
	public Config() {
		super("&8&l[&6Votes&8&l]&r ");
	}
	
}
