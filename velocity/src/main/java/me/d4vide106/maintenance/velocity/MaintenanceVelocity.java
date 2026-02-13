package me.d4vide106.maintenance.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceProvider;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseFactory;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.redis.RedisManager;
import me.d4vide106.maintenance.redis.RedisMessage;
import me.d4vide106.maintenance.velocity.command.MaintenanceCommand;
import me.d4vide106.maintenance.velocity.listener.ConnectionListener;
import me.d4vide106.maintenance.velocity.listener.ProxyPingListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Main plugin class for Velocity proxy.
 * Also compatible with BungeeCord and Waterfall.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
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
    private RedisManager redisManager;
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
            
            // Initialize Redis (optional)
            if (config.isRedisEnabled()) {
                try {
                    redisManager = new RedisManager(
                        config.getRedisHost(),
                        config.getRedisPort(),
                        config.getRedisPassword(),
                        config.getRedisDatabase(),
                        config.getRedisChannel()
                    );
                    redisManager.initialize().join();
                    redisManager.subscribe(this::handleRedisMessage);
                    logger.info("Redis sync enabled");
                } catch (Exception e) {
                    logger.warn("Failed to initialize Redis: {}", e.getMessage());
                    redisManager = null;
                }
            }
            
            // Server identification for Redis
            String serverName = "velocity-proxy";
            
            // Initialize managers
            maintenanceManager = new MaintenanceManager(database);
            whitelistManager = new WhitelistManager(database, redisManager, serverName);
            timerManager = new TimerManager(redisManager, serverName);
            
            // Initialize whitelist cache
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
            logger.info("  ✅ MaintenanceUniversal v1.0.0 enabled successfully!");
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
        
        if (redisManager != null) {
            redisManager.shutdown().join();
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
    
    private void handleRedisMessage(@NotNull RedisMessage message) {
        // Handle Redis messages for cross-server sync
        switch (message.getType()) {
            case MAINTENANCE_ENABLED:
                String modeStr = message.getData().get("mode");
                MaintenanceMode mode = MaintenanceMode.valueOf(modeStr != null ? modeStr : "GLOBAL");
                maintenanceManager.enable(
                    mode,
                    message.getData().get("reason")
                );
                break;
            
            case MAINTENANCE_DISABLED:
                maintenanceManager.disable();
                break;
            
            case WHITELIST_ADDED:
            case WHITELIST_REMOVED:
            case WHITELIST_CLEARED:
                whitelistManager.refresh();
                break;
            
            case CONFIG_RELOAD:
                try {
                    config.load();
                    logger.info("Configuration reloaded from Redis signal");
                } catch (Exception e) {
                    logger.warn("Failed to reload config: {}", e.getMessage());
                }
                break;
            
            case TIMER_SCHEDULED:
            case TIMER_CANCELLED:
                // Timer events handled by TimerManager
                break;
        }
    }
    
    private void detectProxyType() {
        logger.info("════════════════════════════════════════════════════════════");
        logger.info("  Proxy Platform Detection");
        logger.info("════════════════════════════════════════════════════════════");
        logger.info("  Type: Velocity");
        logger.info("  Version: {}", server.getVersion().getVersion());
        logger.info("  Compatible: Velocity, BungeeCord, Waterfall");
        logger.info("  Proxy Mode: {}", config != null && config.isProxyMode() ? "Enabled" : "Disabled");
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