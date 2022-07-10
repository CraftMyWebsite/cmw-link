package fr.AxelVatan.CMWLink.Common.WebServer;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import lombok.Getter;

public abstract class CMWLRoute<PluginType extends CMWLPackage> implements IRoute{
	
	private @Getter PluginType plugin;
	private @Getter String routeName;
	private @Getter RouteType routeType;
	
	public CMWLRoute(PluginType plugin, String routeName, RouteType routeType) {
		this.plugin = plugin;
		this.routeName = routeName;
		this.routeType = routeType;
	}

}
