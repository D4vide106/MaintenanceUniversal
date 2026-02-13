package me.d4vide106.maintenance.forge;

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
import me.d4vide106.maintenance.forge.command.MaintenanceCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Main mod class for Forge and NeoForge.
 * Compatible with both loaders.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
@Mod("maintenanceuniversal")
public class MaintenanceForge {
    
    public static final String MOD_ID = "maintenanceuniversal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static MaintenanceForge instance;
    
    private MaintenanceConfig config;
    private DatabaseProvider database;
    private MaintenanceManager maintenanceManager;
    private WhitelistManager whitelistManager;
    private TimerManager timerManager;
    private RedisManager redisManager;
    private MaintenanceAPIImpl apiImpl;
    
    public MaintenanceForge() {
        instance = this;
        
        // Register ourselves for server events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        printBanner();
        detectModLoader();
        
        try {
            // Load configuration
            Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
            config = new MaintenanceConfig(configPath);
            config.load();
            LOGGER.info("Configuration loaded successfully");
            
            // Initialize database
            database = DatabaseFactory.create(config, configPath);
            database.initialize().join();
            LOGGER.info("Database initialized: {}", config.getDatabaseType());
            
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
                    LOGGER.info("Redis sync enabled");
                } catch (Exception e) {
                    LOGGER.warn("Failed to initialize Redis: {}", e.getMessage());
                    redisManager = null;
                }
            }
            
            // Server identification for Redis
            String serverName = "forge-server";
            
            // Initialize managers
            maintenanceManager = new MaintenanceManager(database);
            whitelistManager = new WhitelistManager(database, redisManager, serverName);
            timerManager = new TimerManager(redisManager, serverName);
            
            // Initialize whitelist cache
            whitelistManager.initialize().join();
            LOGGER.info("Managers initialized");
            
            // Initialize API
            apiImpl = new MaintenanceAPIImpl(
                this,
                config,
                maintenanceManager,
                whitelistManager,
                timerManager
            );
            MaintenanceProvider.register(apiImpl);
            LOGGER.info("API registered");
            
            LOGGER.info("════════════════════════════════════════════════════════════");
            LOGGER.info("  ✅ MaintenanceUniversal v1.0.0 loaded successfully!");
            LOGGER.info("════════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize mod", e);
        }
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        MaintenanceCommand.register(event.getDispatcher(), apiImpl);
        LOGGER.info("Commands registered");
    }
    
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("Shutting down MaintenanceUniversal...");
        
        if (timerManager != null) {
            timerManager.shutdown();
        }
        
        if (redisManager != null) {
            redisManager.shutdown().join();
        }
        
        if (database != null) {
            database.shutdown().join();
        }
        
        LOGGER.info("MaintenanceUniversal shut down");
    }
    
    private void handleRedisMessage(@NotNull RedisMessage message) {
        switch (message.getType()) {
            case MAINTENANCE_ENABLED:
                String modeStr = message.getData().get("mode");
                MaintenanceMode mode = MaintenanceMode.valueOf(modeStr != null ? modeStr : "GLOBAL");
                maintenanceManager.enable(mode, message.getData().get("reason"));
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
                    LOGGER.info("Configuration reloaded from Redis signal");
                } catch (Exception e) {
                    LOGGER.warn("Failed to reload config: {}", e.getMessage());
                }
                break;
            
            case TIMER_SCHEDULED:
            case TIMER_CANCELLED:
                break;
        }
    }
    
    private void detectModLoader() {
        LOGGER.info("════════════════════════════════════════════════════════════");
        LOGGER.info("  Mod Loader Detection");
        LOGGER.info("════════════════════════════════════════════════════════════");
        
        String loader = "Forge";
        try {
            // Try to detect NeoForge
            Class.forName("net.neoforged.fml.common.Mod");
            loader = "NeoForge";
        } catch (ClassNotFoundException ignored) {}
        
        LOGGER.info("  Type: {}", loader);
        LOGGER.info("  Compatible: Forge, NeoForge");
        LOGGER.info("════════════════════════════════════════════════════════════");
    }
    
    private void printBanner() {
        LOGGER.info("");
        LOGGER.info("  __  __       _       _                                  ");
        LOGGER.info(" |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        LOGGER.info(" | |\\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        LOGGER.info(" | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        LOGGER.info(" |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        LOGGER.info("");
        LOGGER.info("  Universal Maintenance Mod v1.0.0 - Forge");
        LOGGER.info("  Author: D4vide106");
        LOGGER.info("");
    }
    
    public static MaintenanceForge getInstance() {
        return instance;
    }
    
    public MaintenanceConfig getConfig() {
        return config;
    }
    
    public DatabaseProvider getDatabase() {
        return database;
    }
    
    public MaintenanceAPIImpl getApi() {
        return apiImpl;
    }
}
