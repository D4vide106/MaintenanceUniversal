package me.d4vide106.maintenance.bungee;

import me.d4vide106.maintenance.api.MaintenanceProvider;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseFactory;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.bungee.command.MaintenanceCommand;
import me.d4vide106.maintenance.bungee.listener.ConnectionListener;
import me.d4vide106.maintenance.bungee.listener.ProxyPingListener;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;

public class MaintenanceBungee extends Plugin {
    
    private MaintenanceConfig config;
    private DatabaseProvider database;
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    private MaintenanceAPIImpl apiImpl;
    
    @Override
    public void onEnable() {
        printBanner();
        detectProxyType();
        
        try {
            // Load configuration
            Path dataPath = getDataFolder().toPath();
            config = new MaintenanceConfig(dataPath);
            config.load();
            getLogger().info("Configuration loaded successfully");
            
            // Initialize database
            database = DatabaseFactory.create(config, dataPath);
            database.initialize().join();
            getLogger().info("Database initialized: " + config.getDatabaseType());
            
            // Initialize managers
            maintenanceManager = new MaintenanceManager();
            whitelistManager = new WhitelistManager();
            timerManager = new TimerManager();
            
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
            registerCommands();
            getLogger().info("Commands registered");
            
            getLogger().info("════════════════════════════════════════════════════════════");
            getLogger().info("  ✅ MaintenanceUniversal v1.0.0 enabled!");
            getLogger().info("════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            getLogger().severe("Failed to enable plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down MaintenanceUniversal...");
        
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        if (database != null) {
            database.shutdown().join();
        }
        
        getLogger().info("MaintenanceUniversal disabled");
    }
    
    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this, apiImpl, config, whitelistManager, database));
        getProxy().getPluginManager().registerListener(this, new ProxyPingListener(apiImpl, config));
    }
    
    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand(this, apiImpl));
    }
    
    private void detectProxyType() {
        getLogger().info("════════════════════════════════════════════════════════════");
        getLogger().info("  Proxy Platform Detection");
        getLogger().info("════════════════════════════════════════════════════════════");
        
        String type = "BungeeCord";
        try {
            Class.forName("io.github.waterfallmc.waterfall.conf.WaterfallConfiguration");
            type = "Waterfall";
        } catch (ClassNotFoundException ignored) {}
        
        getLogger().info("  Type: " + type);
        getLogger().info("  Version: " + getProxy().getVersion());
        getLogger().info("  Compatible: BungeeCord, Waterfall");
        getLogger().info("════════════════════════════════════════════════════════════");
    }
    
    private void printBanner() {
        getLogger().info("");
        getLogger().info("  __  __       _       _                                  ");
        getLogger().info(" |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        getLogger().info(" | |\\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        getLogger().info(" | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        getLogger().info(" |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        getLogger().info("");
        getLogger().info("  Universal Maintenance Plugin v1.0.0 - BungeeCord");
        getLogger().info("  Author: D4vide106");
        getLogger().info("");
    }
    
    public MaintenanceConfig getMaintenanceConfig() {
        return config;
    }
    
    public DatabaseProvider getDatabase() {
        return database;
    }
}
