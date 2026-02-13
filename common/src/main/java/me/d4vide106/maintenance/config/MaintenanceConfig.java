package me.d4vide106.maintenance.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Main configuration manager for Maintenance Universal.
 * <p>
 * Handles loading, saving, and accessing configuration values.
 * Uses Configurate for robust YAML parsing with comments preservation.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceConfig {
    
    private final Path configPath;
    private YamlConfigurationLoader loader;
    private ConfigurationNode root;
    
    public MaintenanceConfig(@NotNull Path dataFolder) {
        this.configPath = dataFolder.resolve("config.yml");
    }
    
    /**
     * Loads or creates the configuration file.
     * 
     * @throws IOException if file operations fail
     */
    public void load() throws IOException {
        // Create data folder if not exists
        if (!Files.exists(configPath.getParent())) {
            Files.createDirectories(configPath.getParent());
        }
        
        // Create default config if not exists
        if (!Files.exists(configPath)) {
            createDefaultConfig();
        }
        
        loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .build();
        
        root = loader.load();
    }
    
    /**
     * Saves the current configuration to disk.
     */
    public void save() throws IOException {
        loader.save(root);
    }
    
    /**
     * Reloads the configuration from disk.
     */
    public void reload() throws IOException {
        root = loader.load();
    }
    
    /**
     * Creates the default configuration file.
     */
    private void createDefaultConfig() throws IOException {
        loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .build();
        
        root = loader.createNode();
        
        // General settings
        root.node("settings", "enabled").set(false).comment("Enable maintenance mode on startup");
        root.node("settings", "mode").set("GLOBAL").comment("Mode: GLOBAL, SERVER_SPECIFIC, SCHEDULED, EMERGENCY");
        root.node("settings", "kick-on-enable").set(true).comment("Kick non-whitelisted players when enabling");
        root.node("settings", "update-checker").set(true).comment("Check for plugin updates");
        root.node("settings", "debug").set(false).comment("Enable debug logging");
        
        // Messages
        root.node("messages", "kick-message").set(
            "<gradient:red:dark_red><bold>⚠ MAINTENANCE MODE ⚠</bold></gradient>\n" +
            "<gray>The server is currently undergoing maintenance.</gray>\n" +
            "<yellow>Please try again later!</yellow>"
        );
        
        root.node("messages", "join-deny").set(
            "<red>The server is in maintenance mode!</red>\n" +
            "<gray>You do not have permission to join.</gray>"
        );
        
        root.node("messages", "motd-line1").set("<gradient:red:yellow>⚠ MAINTENANCE MODE ⚠</gradient>");
        root.node("messages", "motd-line2").set("<gray>Server is currently offline for maintenance</gray>");
        
        root.node("messages", "maintenance-enabled").set("<green>Maintenance mode has been enabled!</green>");
        root.node("messages", "maintenance-disabled").set("<green>Maintenance mode has been disabled!</green>");
        
        root.node("messages", "timer-warning").set(
            "<gold><bold>MAINTENANCE NOTICE</bold></gold>\n" +
            "<yellow>Maintenance will begin in {time}</yellow>\n" +
            "<gray>Please finish your activities and logout safely.</gray>"
        );
        
        root.node("messages", "timer-started").set("<red>Maintenance has started! Kicked: {count} players</red>");
        
        // Whitelist settings
        root.node("whitelist", "permission-bypass").set(true).comment("Allow bypass with permission");
        root.node("whitelist", "bypass-permission").set("maintenance.bypass");
        root.node("whitelist", "clear-on-disable").set(false).comment("Clear whitelist when disabling maintenance");
        
        // Timer settings
        root.node("timer", "warning-intervals").setList(Integer.class, List.of(600, 300, 180, 60, 30, 10));
        root.node("timer", "warning-intervals").comment("Warning intervals in seconds before maintenance starts");
        
        root.node("timer", "enable-countdown").set(true);
        root.node("timer", "countdown-action-bar").set(true).comment("Show countdown in action bar");
        
        // Database settings
        root.node("database", "type").set("SQLITE").comment("Database type: SQLITE, MYSQL, MARIADB");
        root.node("database", "host").set("localhost");
        root.node("database", "port").set(3306);
        root.node("database", "database").set("maintenance");
        root.node("database", "username").set("root");
        root.node("database", "password").set("password");
        root.node("database", "pool-size").set(10);
        
        // Redis settings (for multi-server sync)
        root.node("redis", "enabled").set(false).comment("Enable Redis for multi-server synchronization");
        root.node("redis", "host").set("localhost");
        root.node("redis", "port").set(6379);
        root.node("redis", "password").set("");
        root.node("redis", "database").set(0);
        root.node("redis", "channel").set("maintenance");
        
        // Server list (MOTD) settings
        root.node("server-list", "enabled").set(true).comment("Modify server list during maintenance");
        root.node("server-list", "show-version").set(true);
        root.node("server-list", "version-text").set("§c⚠ MAINTENANCE ⚠");
        root.node("server-list", "show-players").set(true);
        root.node("server-list", "max-players").set(0);
        root.node("server-list", "player-count").set(0);
        root.node("server-list", "custom-icon").set(false);
        root.node("server-list", "icon-path").set("maintenance-icon.png");
        
        // Discord webhook
        root.node("discord", "enabled").set(false);
        root.node("discord", "webhook-url").set("");
        root.node("discord", "notify-enable").set(true);
        root.node("discord", "notify-disable").set(true);
        root.node("discord", "notify-scheduled").set(true);
        root.node("discord", "embed-color").set("#FF5555");
        root.node("discord", "footer-text").set("Maintenance Universal by D4vide106");
        
        // Statistics
        root.node("statistics", "enabled").set(true).comment("Track maintenance statistics");
        root.node("statistics", "bstats").set(true).comment("Send anonymous usage data to bStats");
        
        loader.save(root);
    }
    
    // ============================================
    // GETTERS
    // ============================================
    
    public boolean isEnabledOnStartup() {
        return root.node("settings", "enabled").getBoolean(false);
    }
    
    public String getMode() {
        return root.node("settings", "mode").getString("GLOBAL");
    }
    
    public boolean isKickOnEnable() {
        return root.node("settings", "kick-on-enable").getBoolean(true);
    }
    
    public boolean isUpdateChecker() {
        return root.node("settings", "update-checker").getBoolean(true);
    }
    
    public boolean isDebug() {
        return root.node("settings", "debug").getBoolean(false);
    }
    
    public String getKickMessage() {
        return root.node("messages", "kick-message").getString("");
    }
    
    public String getJoinDenyMessage() {
        return root.node("messages", "join-deny").getString("");
    }
    
    public String getMOTDLine1() {
        return root.node("messages", "motd-line1").getString("");
    }
    
    public String getMOTDLine2() {
        return root.node("messages", "motd-line2").getString("");
    }
    
    public String getMaintenanceEnabledMessage() {
        return root.node("messages", "maintenance-enabled").getString("");
    }
    
    public String getMaintenanceDisabledMessage() {
        return root.node("messages", "maintenance-disabled").getString("");
    }
    
    public String getTimerWarningMessage() {
        return root.node("messages", "timer-warning").getString("");
    }
    
    public String getTimerStartedMessage() {
        return root.node("messages", "timer-started").getString("");
    }
    
    public boolean isPermissionBypass() {
        return root.node("whitelist", "permission-bypass").getBoolean(true);
    }
    
    public String getBypassPermission() {
        return root.node("whitelist", "bypass-permission").getString("maintenance.bypass");
    }
    
    public boolean isClearWhitelistOnDisable() {
        return root.node("whitelist", "clear-on-disable").getBoolean(false);
    }
    
    public List<Integer> getWarningIntervals() {
        try {
            return root.node("timer", "warning-intervals").getList(Integer.class, new ArrayList<>());
        } catch (Exception e) {
            return List.of(600, 300, 180, 60, 30, 10);
        }
    }
    
    public boolean isCountdownEnabled() {
        return root.node("timer", "enable-countdown").getBoolean(true);
    }
    
    public boolean isCountdownActionBar() {
        return root.node("timer", "countdown-action-bar").getBoolean(true);
    }
    
    public String getDatabaseType() {
        return root.node("database", "type").getString("SQLITE");
    }
    
    public String getDatabaseHost() {
        return root.node("database", "host").getString("localhost");
    }
    
    public int getDatabasePort() {
        return root.node("database", "port").getInt(3306);
    }
    
    public String getDatabaseName() {
        return root.node("database", "database").getString("maintenance");
    }
    
    public String getDatabaseUsername() {
        return root.node("database", "username").getString("root");
    }
    
    public String getDatabasePassword() {
        return root.node("database", "password").getString("password");
    }
    
    public int getDatabasePoolSize() {
        return root.node("database", "pool-size").getInt(10);
    }
    
    public boolean isRedisEnabled() {
        return root.node("redis", "enabled").getBoolean(false);
    }
    
    public String getRedisHost() {
        return root.node("redis", "host").getString("localhost");
    }
    
    public int getRedisPort() {
        return root.node("redis", "port").getInt(6379);
    }
    
    public String getRedisPassword() {
        return root.node("redis", "password").getString("");
    }
    
    public int getRedisDatabase() {
        return root.node("redis", "database").getInt(0);
    }
    
    public String getRedisChannel() {
        return root.node("redis", "channel").getString("maintenance");
    }
    
    public boolean isServerListEnabled() {
        return root.node("server-list", "enabled").getBoolean(true);
    }
    
    public boolean isShowVersion() {
        return root.node("server-list", "show-version").getBoolean(true);
    }
    
    public String getVersionText() {
        return root.node("server-list", "version-text").getString("§c⚠ MAINTENANCE ⚠");
    }
    
    public boolean isDiscordEnabled() {
        return root.node("discord", "enabled").getBoolean(false);
    }
    
    public String getDiscordWebhookUrl() {
        return root.node("discord", "webhook-url").getString("");
    }
    
    public boolean isDiscordNotifyEnable() {
        return root.node("discord", "notify-enable").getBoolean(true);
    }
    
    public boolean isDiscordNotifyDisable() {
        return root.node("discord", "notify-disable").getBoolean(true);
    }
    
    public boolean isStatisticsEnabled() {
        return root.node("statistics", "enabled").getBoolean(true);
    }
    
    public boolean isBStatsEnabled() {
        return root.node("statistics", "bstats").getBoolean(true);
    }
}