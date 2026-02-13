package me.d4vide106.maintenance.paper;

import me.d4vide106.maintenance.api.MaintenanceProvider;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.paper.command.MaintenanceCommand;
import me.d4vide106.maintenance.paper.listener.ConnectionListener;
import me.d4vide106.maintenance.paper.listener.ServerListPingListener;
import me.d4vide106.maintenance.paper.util.VersionAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Main plugin class for Paper/Spigot/Bukkit/Purpur/Folia.
 */
public class MaintenancePaper extends JavaPlugin {
    
    private MaintenanceConfig config;
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    private MaintenanceAPIImpl apiImpl;
    
    @Override
    public void onEnable() {
        printBanner();
        VersionAdapter.printVersionInfo(getLogger());
        
        try {
            // Load configuration
            config = new MaintenanceConfig(getDataFolder().toPath());
            config.load();
            getLogger().info("Configuration loaded successfully");
            
            // Initialize managers (simplified - no database)
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
            MaintenanceCommand command = new MaintenanceCommand(this, apiImpl);
            getCommand("maintenance").setExecutor(command);
            getCommand("maintenance").setTabCompleter(command);
            getLogger().info("Commands registered");
            
            getLogger().info("════════════════════════════════════════════════════════════");
            getLogger().info("  ✅ MaintenanceUniversal v" + getPluginMeta().getVersion() + " enabled!");
            getLogger().info("════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down MaintenanceUniversal...");
        
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        getLogger().info("MaintenanceUniversal disabled");
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
            new ConnectionListener(this, apiImpl, config),
            this
        );
        
        getServer().getPluginManager().registerEvents(
            new ServerListPingListener(apiImpl, config, new File(getDataFolder(), "icon.png")),
            this
        );
    }
    
    public MaintenanceConfig getConfig() {
        return config;
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
