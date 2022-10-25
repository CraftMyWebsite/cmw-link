package fr.AxelVatan.CMWLink.Votes.VL;

import java.util.logging.Level;

import com.velocitypowered.api.proxy.ProxyServer;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import fr.AxelVatan.CMWLink.Votes.Common.Config;
import fr.AxelVatan.CMWLink.Votes.VL.Routes.VoteReceived;
import lombok.Getter;

public class Main extends CMWLPackage{

	private @Getter ProxyServer proxy;
	private @Getter Config config;
	
	@com.google.inject.Inject
    public Main(
            ProxyServer proxy
    ) {
        this.proxy = proxy;
    }

	
	@Override
	public void enable() {
		this.log(Level.INFO, "Votes for Velocity enabled.");
		this.config = new Config();
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		this.addRoute(new VoteReceived(this));
	}
}
