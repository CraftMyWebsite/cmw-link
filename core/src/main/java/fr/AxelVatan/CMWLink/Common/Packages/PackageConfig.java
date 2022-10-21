package fr.AxelVatan.CMWLink.Common.Packages;

import lombok.Getter;

public class PackageConfig {

	private @Getter String prefix = "[Unknown]";
	
	public PackageConfig(String prefix) {
		this.prefix = prefix;
	}
}
