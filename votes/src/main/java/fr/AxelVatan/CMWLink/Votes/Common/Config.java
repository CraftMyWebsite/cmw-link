package fr.AxelVatan.CMWLink.Votes.Common;

import fr.AxelVatan.CMWLink.Common.Packages.PackageConfig;
import lombok.Getter;

public class Config extends PackageConfig{

	private @Getter String sendVoteText = "{username} � voter pour le site {site_id}";
	
	public Config() {
		super("[Votes]");
	}
	
}
