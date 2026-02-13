package me.d4vide106.maintenance.paper;

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
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Main plugin class for Paper/Spigot implementation.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenancePaper extends JavaPlugin {
    
    private MaintenanceConfig config;
    private DatabaseProvider database;
    private RedisManager redis;
    
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    
    private MaintenanceAPIImpl apiImpl;
    private MaintenancePlaceholderExpansion placeholderExpansion;
    
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        
        // Print banner
        printBanner();
        
        // Print version info
        VersionAdapter.printVersionInfo();
        
        try {
            // Load configuration
            getLogger().info("Loading configuration...");
            loadConfiguration();
            
            // Initialize database
            getLogger().info("Initializing database (" + config.getDatabaseType() + ")...");
            initializeDatabase();
            
            // Initialize Redis (if enabled)
            if (config.isRedisEnabled()) {
                getLogger().info("Connecting to Redis...");
                initializeRedis();
            }
            
            // Initialize managers
            getLogger().info("Initializing managers...");
            initializeManagers();
            
            // Register API
            getLogger().info("Registering API...");
            registerAPI();
            
            // Register commands
            getLogger().info("Registering commands...");
            registerCommands();
            
            // Register listeners
            getLogger().info("Registering listeners...");
            registerListeners();
            
            // Register PlaceholderAPI (if available)
            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                getLogger().info("Hooking into PlaceholderAPI...");
                registerPlaceholders();
            }
            
            // Initialize bStats
            if (config.isBStatsEnabled()) {
                new Metrics(this, 12345); // TODO: Get real bStats ID
                getLogger().info("bStats metrics enabled");
            }
            
            long loadTime = System.currentTimeMillis() - startTime;
            getLogger().info("");
            getLogger().info("§a┌─────────────────────────────────────────────────┐");
            getLogger().info("§a│                                                 │");
            getLogger().info("§a│  §6§l✓ MaintenanceUniversal Enabled Successfully!   §a│");
            getLogger().info("§a│                                                 │");
            getLogger().info(String.format("§a│  §7Version: §f%-36s§a│", getDescription().getVersion()));
            getLogger().info(String.format("§a│  §7Platform: §f%-35s§a│", "Paper/Spigot"));
            getLogger().info(String.format("§a│  §7MC Version: §f%-33s§a│", VersionAdapter.getVersion()));
            getLogger().info(String.format("§a│  §7Load Time: §f%-33s§a│", loadTime + "ms"));
            getLogger().info("§a│                                                 │");
            getLogger().info("§a└─────────────────────────────────────────────────┘");
            getLogger().info("");
            
            // Log maintenance status
            if (maintenanceManager.isEnabled()) {
                getLogger().warning("⚠ Maintenance mode is currently ENABLED");
                getLogger().warning("Mode: " + maintenanceManager.getMode());
                String reason = maintenanceManager.getReason();
                if (reason != null) {
                    getLogger().warning("Reason: " + reason);
                }
            }
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable MaintenanceUniversal!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down MaintenanceUniversal...");
        
        // Unregister PlaceholderAPI
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        
        // Unregister API
        MaintenanceProvider.unregister();
        
        // Shutdown managers
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        // Close database
        if (database != null) {
            try {
                database.shutdown().join();
            } catch (Exception e) {
                getLogger().warning("Error closing database: " + e.getMessage());
            }
        }
        
        // Close Redis
        if (redis != null) {
            try {
                redis.shutdown().join();
            } catch (Exception e) {
                getLogger().warning("Error closing Redis: " + e.getMessage());
            }
        }
        
        getLogger().info("§cMaintenanceUniversal disabled.");
    }
    
    private void printBanner() {
        getLogger().info("");
        getLogger().info("§6  __  __       _       _                                  ");
        getLogger().info("§6 |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        getLogger().info("§6 | |\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        getLogger().info("§6 | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        getLogger().info("§6 |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        getLogger().info("");
        getLogger().info("§7         §fUniversal §8§l» §7Professional Maintenance System");
        getLogger().info("§7         §fAuthor: §eD4vide106");
        getLogger().info("");
    }
    
    private void loadConfiguration() throws IOException {
        config = new MaintenanceConfig(getDataFolder().toPath());
        config.load();
    }
    
    private void initializeDatabase() {
        database = DatabaseFactory.create(config, getDataFolder());
        database.initialize().join();
    }
    
    private void initializeRedis() {
        redis = new RedisManager(
            config.getRedisHost(),
            config.getRedisPort(),
            config.getRedisPassword(),
            config.getRedisDatabase(),
            config.getRedisChannel()
        );
        redis.initialize().join();
        
        // Subscribe to Redis messages
        redis.subscribe(message -> {
            getLogger().info("Received Redis message: " + message.getType());
            // Handle cross-server sync
            switch (message.getType()) {
                case MAINTENANCE_ENABLED:
                case MAINTENANCE_DISABLED:
                case WHITELIST_ADDED:
                case WHITELIST_REMOVED:
                case WHITELIST_CLEARED:
                    // Refresh from database
                    maintenanceManager.initialize();
                    whitelistManager.refresh();
                    break;
            }
        });
    }
    
    private void initializeManagers() {
        String serverName = getServer().getName();
        
        maintenanceManager = new MaintenanceManager(database, redis, serverName);
        whitelistManager = new WhitelistManager(database, redis, serverName);
        timerManager = new TimerManager(redis, serverName);
        
        // Initialize managers
        maintenanceManager.initialize().join();
        whitelistManager.initialize().join();
    }
    
    private void registerAPI() {
        apiImpl = new MaintenanceAPIImpl(
            this,
            config,
            maintenanceManager,
            whitelistManager,
            timerManager
        );
        MaintenanceProvider.register(apiImpl);
    }
    
    private void registerCommands() {
        MaintenanceCommand command = new MaintenanceCommand(this, config, apiImpl);
        getCommand("maintenance").setExecutor(command);
        getCommand("maintenance").setTabCompleter(command);
    }
    
    private void registerListeners() {
        // Connection listener
        getServer().getPluginManager().registerEvents(
            new ConnectionListener(this, config, maintenanceManager, whitelistManager),
            this
        );
        
        // Server list ping listener (MOTD)
        getServer().getPluginManager().registerEvents(
            new ServerListPingListener(this, config, maintenanceManager),
            this
        );
    }
    
    private void registerPlaceholders() {
        placeholderExpansion = new MaintenancePlaceholderExpansion(this);
        if (placeholderExpansion.register()) {
            getLogger().info("✓ PlaceholderAPI expansion registered");
        } else {
            getLogger().warning("✗ Failed to register PlaceholderAPI expansion");
        }
    }
    
    // Getters
    
    @NotNull
    public MaintenanceConfig getMaintenanceConfig() {
        return config;
    }
    
    @NotNull
    public DatabaseProvider getDatabase() {
        return database;
    }
    
    @NotNull
    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }
    
    @NotNull
    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
    
    @NotNull
    public TimerManager getTimerManager() {
        return timerManager;
    }
}