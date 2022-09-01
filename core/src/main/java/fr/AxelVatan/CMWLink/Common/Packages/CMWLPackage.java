package fr.AxelVatan.CMWLink.Common.Packages;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.AxelVatan.CMWLink.Common.WebServer.IRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import lombok.Getter;

public abstract class CMWLPackage {

	private @Getter String pluginName;
	private @Getter String routePrefix;
	private @Getter String version;
	private Logger log;
	private WebServer webServer;
	private @Getter boolean isUseProxy;
	
	public void init(String pluginName, String routePrefix, String version, Logger log, WebServer webServer) {
		this.pluginName = pluginName;
		this.routePrefix = routePrefix;
		this.version = version;
		this.log = log;
		this.webServer = webServer;
		this.isUseProxy = webServer.getConfig().getConfig().isUseProxy();
		onEnable();
	}
	
	public final void onEnable(){
		long epoch = System.currentTimeMillis();
		enable();
		registerRoutes();
		log(Level.INFO, "Enabled in " + convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.MILLISECONDS) + ".");
	}
	
	public final void onDisable(){
		disable();
		log(Level.INFO, "Disabled.");
	}
	
	/**
	 * Called when Package is enabling
	 */
	public abstract void enable();

	/**
	 * Called when Package is disabling
	 */
	public abstract void disable();
	
	/**
	 * Called when Package is enabling
	 */
	public abstract void registerRoutes();
	
	/**
	 * Add route to WebServer
	 */
	public final void addRoute(IRoute route) {
		this.webServer.addRoute(this, route);
	}
	
	/**
	 * Remove route from WebServer
	 */
	public final void removeRoute(IRoute route) {
		this.webServer.removeRoute(this, route);
	}
	
	/**
	 * Log everything you want with priority level (Display colors to console if is supported).
	 */
	public void log(Level level, String message){
		this.log.log(level, "{" + this.pluginName + ", Version: " + this.version + "}==> " +message);
	}
	
	/**
	 * Convert time in milliseconds to any type you want with decimal
	 */
	public String convertString(long time, int trim, TimeUnit type){
		if (time == -1L) {
			return "Permanent";
		}
		if (type == TimeUnit.FIT) {
			if (time < 60000L) {
				type = TimeUnit.SECONDS;
			} else if (time < 3600000L) {
				type = TimeUnit.MINUTES;
			} else if (time < 86400000L) {
				type = TimeUnit.HOURS;
			} else {
				type = TimeUnit.DAYS;
			}
		}
		if (type == TimeUnit.DAYS) {
			return trim(trim, time / 86400000.0D) + " Jours";
		}
		if (type == TimeUnit.HOURS) {
			return trim(trim, time / 3600000.0D) + " Heures";
		}
		if (type == TimeUnit.MINUTES) {
			return trim(trim, time / 60000.0D) + " Minutes";
		}
		if (type == TimeUnit.SECONDS) {
			return trim(trim, time / 1000.0D) + " Secondes";
		}
		return trim(trim, time) + " Milliseconds";
	}
	
	private double trim(int degree, double d){
		String format = "#.#";
		for (int i = 1; i < degree; i++) {
			format = format + "#";
		}
		DecimalFormat twoDForm = new DecimalFormat(format);
		return Double.valueOf(twoDForm.format(d)).doubleValue();
	}
	
	public enum TimeUnit{
		FIT,
		DAYS,
		HOURS,
		MINUTES,
		SECONDS,
		MILLISECONDS;
	}
}
