package fr.CraftMyWebsite.CMWLink.Velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.CraftMyWebsite.CMWLink.Common.Config.ConfigFile;
import fr.CraftMyWebsite.CMWLink.Common.Utils.StartingFrom;
import lombok.Getter;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "craftmywebsite-link",
        name = "CraftMyWebsite-Link",
        version = "1.0",
        url = "https://craftmywebsite.fr/",
        description = "CraftMyWebsite-Link a java plugin for MC servers",
        authors = {"CraftMyWebsite"}
)
public final class VelocityMain {

    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;
    private @Getter ConfigFile configFile;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    void onProxyInitialization(final ProxyInitializeEvent event) {
        logger.info("==========================================");
        this.configFile = new ConfigFile(
                server,
                StartingFrom.VELOCITY,
                dataDirectory.toFile(),
                logger,
                "1.0",
                this
        );
        logger.info("==========================================");

        CommandMeta meta = server.getCommandManager().metaBuilder("vcmwl").build();
        server.getCommandManager().register(meta, new VL_Commands(this));
    }

    public void resetConfig() {
        this.configFile = new ConfigFile(
                server,
                StartingFrom.VELOCITY,
                dataDirectory.toFile(),
                logger,
                "1.0",
                this
        );
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (this.configFile != null) {
            this.configFile.getWebServer().disable();
            this.configFile.getPackages().disablePackages();
        }
    }
}
