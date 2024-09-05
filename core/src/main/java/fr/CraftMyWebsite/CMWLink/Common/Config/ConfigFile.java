package fr.CraftMyWebsite.CMWLink.Common.Config;

import fr.CraftMyWebsite.CMWLink.Common.Packages.Packages;
import fr.CraftMyWebsite.CMWLink.Common.Utils.StartingFrom;
import fr.CraftMyWebsite.CMWLink.Common.Utils.Utils;
import fr.CraftMyWebsite.CMWLink.Common.WebServer.WebServer;
import fr.CraftMyWebsite.CMWLink.Velocity.VelocityMain;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigFile extends IConfigFile {

    //SPIGOT
    private @Getter Server spServer;
    //BUNGEECORD
    private @Getter net.md_5.bungee.api.ProxyServer bgServer;
    //VELOCITY
    private @Getter com.velocitypowered.api.proxy.ProxyServer vlServer;
    private VelocityMain velocityPlugin;

    private @Getter StartingFrom startingFrom;
    private @Getter File filePath;
    private @Getter Logger log;
    private @Getter String version;
    private @Getter Settings settings;
    private @Getter Utils utils;
    private @Getter Packages packages;
    private @Getter WebServer webServer;
    private Persist persist;

    //			.~~~~.
    //			i====i_
    //			|cccc|_)
    //			|cccc|   GIVE ME A BEER ! <3
    //			`-==-'

    public ConfigFile(Server server, StartingFrom startingFrom, File filePath, Logger log, String version) {
        super(filePath, log);
        this.spServer = server;
        load(startingFrom, filePath, log, version, server.getPort());
    }

    public ConfigFile(net.md_5.bungee.api.ProxyServer server, StartingFrom startingFrom, File filePath, Logger log, String version) {
        super(filePath, log);
        this.bgServer = server;
        load(startingFrom, filePath, log, version, 0000);
    }

    public ConfigFile(com.velocitypowered.api.proxy.ProxyServer server, StartingFrom startingFrom, File filePath, Logger log, String version, VelocityMain plugin) {
        super(filePath, log);
        this.vlServer = server;
        this.velocityPlugin = plugin;
        load(startingFrom, filePath, log, version, 0000);
    }

    public void load(StartingFrom startingFrom, File filePath, Logger log, String version, int port) {
        this.startingFrom = startingFrom;
        this.filePath = filePath;
        this.log = log;
        this.version = version;
        this.utils = new Utils(log, startingFrom);
        this.persist = new Persist(this);
        if (this.utils.init(true)) {
            log.info("Loading configuration...");

            settings = persist.getFile(Settings.class).exists() ? persist.load(Settings.class) : new Settings();
            saveSettings();
            log.info("Configuration loaded successfully !");
            if (settings.isBindToDefaultPort()) {
                log.info("- WebServer binded to default server port");
            } else {
                log.info("- Port: " + settings.getPort());
            }
            if (settings.isUseCustomServerAddress()) {
                log.info("- Use custom address: " + settings.getCustomServerAddress());
            }
            log.info("- Log Requests: " + settings.isLogRequests());
            log.info("- Using proxy: " + settings.isUseProxy());
            log.info("- Load Uncertified packages: " + settings.isLoadUncertifiedPackages());
            if (settings.isEnableWhitelistedIps()) {
                log.info("- Whitelisted IP: " + settings.getWhitelistedIps());
            }
            this.webServer = new WebServer(this);
            switch (this.startingFrom) {
                case BUNGEECORD:
                    this.packages = new Packages(bgServer, log, filePath, webServer, utils);
                    if (this.settings.useProxy) {
                        this.startWebServerSpigot(port);
                    } else {
                        log.severe("UseProxy on this BungeeCord Proxy is not set to true !");
                        log.severe("CMW-Link will be useless");
                    }
                    break;
                case SPIGOT:
                    this.packages = new Packages(spServer, log, filePath, webServer, utils);
                    if (this.settings.isUseProxy()) {
                        log.info("Waiting requests from the proxy");
                    } else {
                        this.startWebServerSpigot(port);
                    }
                    break;
                case VELOCITY:
                    this.packages = new Packages(vlServer, log, filePath, webServer, utils);
                    if (this.settings.useProxy) {
                        this.startWebServerVelocity(port);
                    } else {
                        log.severe("UseProxy on this Velocity Proxy is not set to true !");
                        log.severe("CMW-Link will be useless");
                    }
                    break;
            }
        }
    }

    public void saveSettings() {
        if (settings != null) persist.save(settings);
    }

    private void startWebServerSpigot(int port) {
        this.webServer.createRoutes();

        this.webServer.listenPort();
        new BukkitRunnable() {
            @Override
            public void run() {
                webServer.startWebServer(port);
            }
        }.runTaskAsynchronously(Objects.requireNonNull(this.getSpServer().getPluginManager().getPlugin("CraftMyWebsite_Link")));
    }

    private void startWebServerVelocity(int port) {
        this.webServer.createRoutes();

        this.webServer.listenPort();
        this.vlServer.getScheduler()
                .buildTask(this.velocityPlugin, () -> {
                    this.webServer.startWebServer(port);
                })
                .schedule();
    }

    public class Settings {

        private @Getter int port = 24102;
        private @Getter boolean bindToDefaultPort = false;
        private @Getter boolean useCustomServerAddress = false;
        private @Getter String customServerAddress = "123.123.123.123";
        private @Getter boolean loadUncertifiedPackages = false;
        private @Getter boolean logRequests = true;
        private @Getter boolean useProxy = false;
        private @Getter boolean enableWhitelistedIps = false;
        private @Getter List<String> whitelistedIps = Arrays.asList("127.0.0.1");
        private @Getter
        @Setter String token = "TO_GENERATE";
        private @Getter
        @Setter String domain = "TO_GENERATE";
    }
}
