package me.d4vide106.maintenance.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.d4vide106.maintenance.api.MaintenanceProvider;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseFactory;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.velocity.command.MaintenanceCommand;
import me.d4vide106.maintenance.velocity.listener.ConnectionListener;
import me.d4vide106.maintenance.velocity.listener.ProxyPingListener;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "maintenanceuniversal",
    name = "MaintenanceUniversal",
    version = "1.0.0",
    description = "Professional maintenance management for proxies",
    authors = {"D4vide106"},
    url = "https://github.com/D4vide106/MaintenanceUniversal"
)
public class MaintenanceVelocity {
    
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    
    private MaintenanceConfig config;
    private DatabaseProvider database;
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    private MaintenanceAPIImpl apiImpl;
    
    @Inject
    public MaintenanceVelocity(
        ProxyServer server,
        Logger logger,
        @DataDirectory Path dataDirectory
    ) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        printBanner();
        detectProxyType();
        
        try {
            // Load configuration
            config = new MaintenanceConfig(dataDirectory);
            config.load();
            logger.info("Configuration loaded successfully");
            
            // Initialize database
            database = DatabaseFactory.create(config, dataDirectory);
            database.initialize().join();
            logger.info("Database initialized: {}", config.getDatabaseType());
            
            // Initialize managers
            maintenanceManager = new MaintenanceManager();
            whitelistManager = new WhitelistManager();
            timerManager = new TimerManager();
            
            whitelistManager.initialize().join();
            logger.info("Managers initialized");
            
            // Initialize API
            apiImpl = new MaintenanceAPIImpl(
                this,
                config,
                maintenanceManager,
                whitelistManager,
                timerManager
            );
            MaintenanceProvider.register(apiImpl);
            logger.info("API registered");
            
            // Register listeners
            registerListeners();
            
            // Register commands
            registerCommands();
            logger.info("Commands registered");
            
            logger.info("════════════════════════════════════════════════════════════");
            logger.info("  ✅ MaintenanceUniversal v1.0.0 enabled!");
            logger.info("════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            logger.error("Failed to enable plugin", e);
        }
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("Shutting down MaintenanceUniversal...");
        
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        if (database != null) {
            database.shutdown().join();
        }
        
        logger.info("MaintenanceUniversal disabled");
    }
    
    private void registerListeners() {
        server.getEventManager().register(this, new ConnectionListener(server, apiImpl, config, whitelistManager, database));
        server.getEventManager().register(this, new ProxyPingListener(apiImpl, config));
    }
    
    private void registerCommands() {
        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("maintenance")
                .aliases("mt", "maint")
                .build(),
            new MaintenanceCommand(server, apiImpl).createCommand()
        );
    }
    
    private void detectProxyType() {
        logger.info("════════════════════════════════════════════════════════════");
        logger.info("  Proxy Platform Detection");
        logger.info("════════════════════════════════════════════════════════════");
        logger.info("  Type: Velocity");
        logger.info("  Version: {}", server.getVersion().getVersion());
        logger.info("  Compatible: Velocity, BungeeCord, Waterfall");
        logger.info("════════════════════════════════════════════════════════════");
    }
    
    private void printBanner() {
        logger.info("");
        logger.info("  __  __       _       _                                  ");
        logger.info(" |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        logger.info(" | |\\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        logger.info(" | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        logger.info(" |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        logger.info("");
        logger.info("  Universal Maintenance Plugin v1.0.0 - Velocity");
        logger.info("  Author: D4vide106");
        logger.info("");
    }
    
    public ProxyServer getServer() {
        return server;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public DatabaseProvider getDatabase() {
        return database;
    }
    
    public MaintenanceConfig getConfig() {
        return config;
    }
}
