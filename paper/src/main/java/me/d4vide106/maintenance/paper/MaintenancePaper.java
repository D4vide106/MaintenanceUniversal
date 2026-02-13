package me.d4vide106.maintenance.paper;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceProvider;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseFactory;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.paper.command.MaintenanceCommand;
import me.d4vide106.maintenance.paper.expansion.MaintenancePlaceholderExpansion;
import me.d4vide106.maintenance.paper.listener.ConnectionListener;
import me.d4vide106.maintenance.paper.listener.ServerListPingListener;
import me.d4vide106.maintenance.paper.util.VersionAdapter;
import me.d4vide106.maintenance.redis.RedisManager;
import me.d4vide106.maintenance.redis.RedisMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

/**
 * Main plugin class for Paper/Spigot/Bukkit/Purpur/Folia.
 */
public class MaintenancePaper extends JavaPlugin {
    
    private MaintenanceConfig config;
    private DatabaseProvider database;
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    private RedisManager redisManager;
    private MaintenanceAPIImpl apiImpl;
    private MaintenancePlaceholderExpansion placeholderExpansion;
    
    @Override
    public void onEnable() {
        printBanner();
        VersionAdapter.printVersionInfo(getLogger());
        
        try {
            // Load configuration
            config = new MaintenanceConfig(getDataFolder().toPath());
            config.load();
            getLogger().info("Configuration loaded successfully");
            
            // Initialize database
            database = DatabaseFactory.create(config, getDataFolder().toPath());
            database.initialize().join();
            getLogger().info("Database initialized: " + config.getDatabaseType());
            
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
                    getLogger().info("Redis sync enabled");
                } catch (Exception e) {
                    getLogger().warning("Failed to initialize Redis: " + e.getMessage());
                    redisManager = null;
                }
            }
            
            // Get server name (for Redis identification)
            String serverName = getServer().getName();
            
            // Initialize managers
            maintenanceManager = new MaintenanceManager(database);
            whitelistManager = new WhitelistManager(database, redisManager, serverName);
            timerManager = new TimerManager(redisManager, serverName);
            
            // Initialize whitelist cache
            whitelistManager.initialize().join();
            getLogger().info("Managers initialized");
            
            // Initialize API
            apiImpl = new MaintenanceAPIImpl(
                this,
                config,
                maintenanceManager,
                whitelistManager,
                timerManager
            );
            MaintenanceProvider.register(apiImpl);
            getLogger().info("API registered");
            
            // Register listeners
            registerListeners();
            
            // Register commands
            MaintenanceCommand command = new MaintenanceCommand(this, apiImpl);
            getCommand("maintenance").setExecutor(command);
            getCommand("maintenance").setTabCompleter(command);
            getLogger().info("Commands registered");
            
            // Register PlaceholderAPI expansion
            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                placeholderExpansion = new MaintenancePlaceholderExpansion(this, apiImpl);
                getLogger().info("PlaceholderAPI expansion registered");
            }
            
            getLogger().info("════════════════════════════════════════════════════════════");
            getLogger().info("  ✅ MaintenanceUniversal v" + getPluginMeta().getVersion() + " enabled successfully!");
            getLogger().info("════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down MaintenanceUniversal...");
        
        if (placeholderExpansion != null) {
            // Would call unregister() if available
        }
        
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        if (redisManager != null) {
            redisManager.shutdown().join();
        }
        
        if (database != null) {
            database.shutdown().join();
        }
        
        getLogger().info("MaintenanceUniversal disabled");
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
            new ConnectionListener(this, apiImpl, config, whitelistManager, database),
            this
        );
        
        getServer().getPluginManager().registerEvents(
            new ServerListPingListener(apiImpl, config, new File(getDataFolder(), "icon.png")),
            this
        );
    }
    
    public DatabaseProvider getDatabase() {
        return database;
    }
    
    private void handleRedisMessage(@NotNull RedisMessage message) {
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
                // Reload whitelist from database
                whitelistManager.refresh();
                break;
            
            case CONFIG_RELOAD:
                try {
                    config.load();
                    getLogger().info("Configuration reloaded from Redis signal");
                } catch (Exception e) {
                    getLogger().warning("Failed to reload config: " + e.getMessage());
                }
                break;
            
            case TIMER_SCHEDULED:
            case TIMER_CANCELLED:
                // Timer events handled by TimerManager
                break;
        }
    }
    
    private void printBanner() {
        getLogger().info("");
        getLogger().info("  __  __       _       _                                  ");
        getLogger().info(" |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        getLogger().info(" | |\\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        getLogger().info(" | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        getLogger().info(" |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        getLogger().info("");
        getLogger().info("  Universal Maintenance Plugin v" + getPluginMeta().getVersion());
        getLogger().info("  Author: D4vide106");
        getLogger().info("");
    }
}