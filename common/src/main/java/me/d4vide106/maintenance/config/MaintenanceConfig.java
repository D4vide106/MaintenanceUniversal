package me.d4vide106.maintenance.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration manager for MaintenanceUniversal.
 * Universal configuration for all platforms (Paper, Velocity, Fabric, Forge).
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceConfig {
    
    private final Path configPath;
    private CommentedConfigurationNode root;
    
    public MaintenanceConfig(@NotNull Path dataFolder) throws IOException {
        Files.createDirectories(dataFolder);
        this.configPath = dataFolder.resolve("config.yml");
    }
    
    /**
     * Loads or creates the configuration file.
     */
    public void load() throws IOException {
        if (!Files.exists(configPath)) {
            createDefault();
        }
        
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .build();
        
        root = loader.load();
    }
    
    /**
     * Reloads the configuration from disk.
     */
    public void reload() throws IOException {
        load();
    }
    
    /**
     * Saves the current configuration to disk.
     */
    public void save() throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .build();
        loader.save(root);
    }
    
    private void createDefault() throws IOException {
        // Create default config content
        StringBuilder sb = new StringBuilder();
        sb.append("#════════════════════════════════════════════════════════════════#\n");
        sb.append("#          MaintenanceUniversal Configuration                    #\n");
        sb.append("#                      Version: 1.0.0                            #\n");
        sb.append("#════════════════════════════════════════════════════════════════#\n\n");
        
        sb.append("# Database configuration\n");
        sb.append("database:\n");
        sb.append("  type: 'sqlite'  # sqlite, mysql, postgresql\n");
        sb.append("  host: 'localhost'\n");
        sb.append("  port: 3306\n");
        sb.append("  database: 'maintenance'\n");
        sb.append("  username: 'root'\n");
        sb.append("  password: 'password'\n");
        sb.append("  pool-size: 10\n");
        sb.append("  table-prefix: 'maintenance_'\n\n");
        
        sb.append("# Redis multi-server sync\n");
        sb.append("redis:\n");
        sb.append("  enabled: false\n");
        sb.append("  host: 'localhost'\n");
        sb.append("  port: 6379\n");
        sb.append("  password: ''\n");
        sb.append("  database: 0\n");
        sb.append("  channel: 'maintenance'\n\n");
        
        sb.append("# Maintenance settings\n");
        sb.append("maintenance:\n");
        sb.append("  kick-on-enable: true\n");
        sb.append("  kick-delay: 5  # seconds\n");
        sb.append("  kick-message: '<red><bold>Server Under Maintenance</bold></red>\\n\\n<gray>We are currently performing maintenance.</gray>\\n<gray>Please check back later!</gray>'\n\n");
        
        sb.append("  # Server list (MOTD) customization\n");
        sb.append("  motd:\n");
        sb.append("    enabled: true\n");
        sb.append("    line1: '<red><bold>⚠ MAINTENANCE MODE ⚠</bold></red>'\n");
        sb.append("    line2: '<gray>Scheduled maintenance in progress</gray>'\n\n");
        
        sb.append("  # Version text\n");
        sb.append("  version:\n");
        sb.append("    enabled: false\n");
        sb.append("    text: 'Maintenance'\n\n");
        
        sb.append("  # Max players display\n");
        sb.append("  max-players:\n");
        sb.append("    enabled: false\n");
        sb.append("    value: 0\n\n");
        
        sb.append("  # Server icon\n");
        sb.append("  icon:\n");
        sb.append("    enabled: false\n");
        sb.append("    path: 'maintenance-icon.png'\n\n");
        
        sb.append("  # Bypass join message\n");
        sb.append("  bypass-join-message: '<green><bold>✓</bold> You have bypass permission!</green>\\n<gray>Server is in maintenance mode</gray>'\n\n");
        
        sb.append("# Velocity-specific settings\n");
        sb.append("velocity:\n");
        sb.append("  # Enable proxy-level maintenance\n");
        sb.append("  proxy-mode: true\n");
        sb.append("  # Fallback server for maintenance (optional)\n");
        sb.append("  fallback-server: ''\n");
        sb.append("  # Kick players to fallback server instead of disconnecting\n");
        sb.append("  kick-to-fallback: false\n\n");
        
        sb.append("# Timer and scheduling settings\n");
        sb.append("timer:\n");
        sb.append("  warnings: [300, 180, 60, 30, 10]  # seconds before start\n");
        sb.append("  warning-message: '<yellow><bold>⚠ Maintenance Alert</bold></yellow>\\n<gray>Server maintenance starts in <white>{time}</white></gray>'\n\n");
        
        sb.append("  # Title notifications\n");
        sb.append("  title:\n");
        sb.append("    enabled: true\n");
        sb.append("    fade-in: 10\n");
        sb.append("    stay: 40\n");
        sb.append("    fade-out: 10\n");
        sb.append("    text: '<red><bold>MAINTENANCE</bold></red>'\n");
        sb.append("    subtitle: '<yellow>Starts in {time}</yellow>'\n\n");
        
        sb.append("  # Sound effect\n");
        sb.append("  sound:\n");
        sb.append("    enabled: true\n");
        sb.append("    type: 'BLOCK_NOTE_BLOCK_PLING'\n");
        sb.append("    volume: 1.0\n");
        sb.append("    pitch: 1.0\n\n");
        
        sb.append("# bStats metrics\n");
        sb.append("bstats:\n");
        sb.append("  enabled: true\n\n");
        
        sb.append("# Debug mode\n");
        sb.append("debug: false\n");
        
        Files.writeString(configPath, sb.toString());
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Database Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public String getDatabaseType() {
        return root.node("database", "type").getString("sqlite");
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
    
    public String getDatabaseTablePrefix() {
        return root.node("database", "table-prefix").getString("maintenance_");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Redis Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isRedisEnabled() {
        return root.node("redis", "enabled").getBoolean(false);
    }
    
    public String getRedisHost() {
        return root.node("redis", "host").getString("localhost");
    }
    
    public int getRedisPort() {
        return root.node("redis", "port").getInt(6379);
    }
    
    @Nullable
    public String getRedisPassword() {
        String pwd = root.node("redis", "password").getString("");
        return pwd.isEmpty() ? null : pwd;
    }
    
    public int getRedisDatabase() {
        return root.node("redis", "database").getInt(0);
    }
    
    public String getRedisChannel() {
        return root.node("redis", "channel").getString("maintenance");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Maintenance Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean shouldKickOnEnable() {
        return root.node("maintenance", "kick-on-enable").getBoolean(true);
    }
    
    public int getKickDelay() {
        return root.node("maintenance", "kick-delay").getInt(5);
    }
    
    public String getKickMessage() {
        return root.node("maintenance", "kick-message")
            .getString("<red><bold>Server Under Maintenance</bold></red>\n\n<gray>Please check back later!</gray>");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // MOTD Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isCustomMOTDEnabled() {
        return root.node("maintenance", "motd", "enabled").getBoolean(true);
    }
    
    public String getMaintenanceMOTD() {
        return root.node("maintenance", "motd", "text").getString("<red><bold>⚠ MAINTENANCE MODE ⚠</bold></red>");
    }
    
    public String getMaintenanceMOTDLine1() {
        return root.node("maintenance", "motd", "line1").getString("<red><bold>⚠ MAINTENANCE MODE ⚠</bold></red>");
    }
    
    public String getMaintenanceMOTDLine2() {
        return root.node("maintenance", "motd", "line2").getString("<gray>Scheduled maintenance in progress</gray>");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Version Text Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isCustomVersionEnabled() {
        return root.node("maintenance", "version", "enabled").getBoolean(false);
    }
    
    public String getMaintenanceVersionText() {
        return root.node("maintenance", "version", "text").getString("Maintenance");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Max Players Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isCustomMaxPlayersEnabled() {
        return root.node("maintenance", "max-players", "enabled").getBoolean(false);
    }
    
    public int getMaintenanceMaxPlayers() {
        return root.node("maintenance", "max-players", "value").getInt(0);
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Icon Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isCustomIconEnabled() {
        return root.node("maintenance", "icon", "enabled").getBoolean(false);
    }
    
    public String getCustomIconPath() {
        return root.node("maintenance", "icon", "path").getString("maintenance-icon.png");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Bypass Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public String getBypassJoinMessage() {
        return root.node("maintenance", "bypass-join-message")
            .getString("<green><bold>✓</bold> You have bypass permission!</green>");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Velocity-Specific Configuration
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Gets whether proxy-level maintenance is enabled (Velocity).
     */
    public boolean isProxyMode() {
        return root.node("velocity", "proxy-mode").getBoolean(true);
    }
    
    /**
     * Gets the fallback server name for maintenance (Velocity).
     * Returns null if not configured.
     */
    @Nullable
    public String getFallbackServer() {
        String server = root.node("velocity", "fallback-server").getString("");
        return server.isEmpty() ? null : server;
    }
    
    /**
     * Gets whether to kick players to fallback server instead of disconnecting (Velocity).
     */
    public boolean shouldKickToFallback() {
        return root.node("velocity", "kick-to-fallback").getBoolean(false);
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Timer Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public int[] getWarningIntervals() {
        try {
            List<?> list = root.node("timer", "warnings")
                .getList(Object.class, Arrays.asList(300, 180, 60, 30, 10));
            return list.stream().mapToInt(o -> ((Number)o).intValue()).toArray();
        } catch (SerializationException e) {
            return new int[]{300, 180, 60, 30, 10};
        }
    }
    
    public String getWarningMessage() {
        return root.node("timer", "warning-message")
            .getString("<yellow><bold>⚠ Maintenance Alert</bold></yellow>\n<gray>Maintenance starts in {time}</gray>");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Title Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isTitleEnabled() {
        return root.node("timer", "title", "enabled").getBoolean(true);
    }
    
    public int getTitleFadeIn() {
        return root.node("timer", "title", "fade-in").getInt(10);
    }
    
    public int getTitleStay() {
        return root.node("timer", "title", "stay").getInt(40);
    }
    
    public int getTitleFadeOut() {
        return root.node("timer", "title", "fade-out").getInt(10);
    }
    
    public String getTitleText() {
        return root.node("timer", "title", "text").getString("<red><bold>MAINTENANCE</bold></red>");
    }
    
    public String getSubtitleText() {
        return root.node("timer", "title", "subtitle").getString("<yellow>Starts in {time}</yellow>");
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Sound Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isSoundEnabled() {
        return root.node("timer", "sound", "enabled").getBoolean(true);
    }
    
    public String getSoundType() {
        return root.node("timer", "sound", "type").getString("BLOCK_NOTE_BLOCK_PLING");
    }
    
    public float getSoundVolume() {
        return root.node("timer", "sound", "volume").getFloat(1.0f);
    }
    
    public float getSoundPitch() {
        return root.node("timer", "sound", "pitch").getFloat(1.0f);
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Misc Configuration
    // ═══════════════════════════════════════════════════════════════
    
    public boolean isBStatsEnabled() {
        return root.node("bstats", "enabled").getBoolean(true);
    }
    
    public boolean isDebugEnabled() {
        return root.node("debug").getBoolean(false);
    }
    
    /**
     * Gets the configuration path.
     */
    @NotNull
    public Path getConfigPath() {
        return configPath;
    }
    
    /**
     * Gets the root configuration node.
     */
    @NotNull
    public CommentedConfigurationNode getRoot() {
        return root;
    }
}