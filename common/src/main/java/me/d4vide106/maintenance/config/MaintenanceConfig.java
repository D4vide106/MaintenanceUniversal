package me.d4vide106.maintenance.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration manager for MaintenanceUniversal.
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
    
    private void createDefault() throws IOException {
        // Create default config content
        StringBuilder sb = new StringBuilder();
        sb.append("# MaintenanceUniversal Configuration\n");
        sb.append("# Version: 1.0.0\n\n");
        
        sb.append("database:\n");
        sb.append("  type: 'sqlite'  # sqlite, mysql, postgresql\n");
        sb.append("  host: 'localhost'\n");
        sb.append("  port: 3306\n");
        sb.append("  database: 'maintenance'\n");
        sb.append("  username: 'root'\n");
        sb.append("  password: 'password'\n");
        sb.append("  pool-size: 10\n");
        sb.append("  table-prefix: 'maintenance_'\n\n");
        
        sb.append("redis:\n");
        sb.append("  enabled: false\n");
        sb.append("  host: 'localhost'\n");
        sb.append("  port: 6379\n");
        sb.append("  password: ''\n");
        sb.append("  database: 0\n");
        sb.append("  channel: 'maintenance'\n\n");
        
        sb.append("maintenance:\n");
        sb.append("  kick-on-enable: true\n");
        sb.append("  kick-delay: 5  # seconds\n");
        sb.append("  kick-message: '&cServer is under maintenance!'\n\n");
        
        sb.append("  motd:\n");
        sb.append("    enabled: true\n");
        sb.append("    text: '&c&lMaintenance Mode'\n");
        
        sb.append("  version:\n");
        sb.append("    enabled: false\n");
        sb.append("    text: 'Maintenance'\n");
        
        sb.append("  max-players:\n");
        sb.append("    enabled: false\n");
        sb.append("    value: 0\n");
        
        sb.append("  icon:\n");
        sb.append("    enabled: false\n\n");
        
        sb.append("  bypass-join-message: '&a&lYou have bypass permission!'\n\n");
        
        sb.append("timer:\n");
        sb.append("  warnings: [300, 180, 60, 30, 10]  # seconds before start\n");
        sb.append("  warning-message: '&eMaintenance starts in {time}!'\n");
        sb.append("  title:\n");
        sb.append("    enabled: true\n");
        sb.append("    text: '&c&lMaintenance'\n");
        sb.append("    subtitle: '&eStarts in {time}'\n");
        sb.append("  sound:\n");
        sb.append("    enabled: true\n");
        sb.append("    type: 'BLOCK_NOTE_BLOCK_PLING'\n");
        sb.append("    volume: 1.0\n");
        sb.append("    pitch: 1.0\n\n");
        
        sb.append("bstats:\n");
        sb.append("  enabled: true\n");
        
        Files.writeString(configPath, sb.toString());
    }
    
    // Database
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
    
    // Redis
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
    
    // Maintenance
    public boolean shouldKickOnEnable() {
        return root.node("maintenance", "kick-on-enable").getBoolean(true);
    }
    
    public int getKickDelay() {
        return root.node("maintenance", "kick-delay").getInt(5);
    }
    
    public String getKickMessage() {
        return root.node("maintenance", "kick-message").getString("&cServer is under maintenance!");
    }
    
    // MOTD
    public boolean isCustomMOTDEnabled() {
        return root.node("maintenance", "motd", "enabled").getBoolean(true);
    }
    
    public String getMaintenanceMOTD() {
        return root.node("maintenance", "motd", "text").getString("&c&lMaintenance Mode");
    }
    
    // Version
    public boolean isCustomVersionEnabled() {
        return root.node("maintenance", "version", "enabled").getBoolean(false);
    }
    
    public String getMaintenanceVersionText() {
        return root.node("maintenance", "version", "text").getString("Maintenance");
    }
    
    // Max Players
    public boolean isCustomMaxPlayersEnabled() {
        return root.node("maintenance", "max-players", "enabled").getBoolean(false);
    }
    
    public int getMaintenanceMaxPlayers() {
        return root.node("maintenance", "max-players", "value").getInt(0);
    }
    
    // Icon
    public boolean isCustomIconEnabled() {
        return root.node("maintenance", "icon", "enabled").getBoolean(false);
    }
    
    // Bypass
    public String getBypassJoinMessage() {
        return root.node("maintenance", "bypass-join-message").getString("&a&lYou have bypass permission!");
    }
    
    // Timer
    public int[] getWarningIntervals() {
        List<?> list = root.node("timer", "warnings").getList(Object.class, Arrays.asList(300, 180, 60, 30, 10));
        return list.stream().mapToInt(o -> ((Number)o).intValue()).toArray();
    }
    
    public String getWarningMessage() {
        return root.node("timer", "warning-message").getString("&eMaintenance starts in {time}!");
    }
    
    public boolean isTitleEnabled() {
        return root.node("timer", "title", "enabled").getBoolean(true);
    }
    
    public String getTitleText() {
        return root.node("timer", "title", "text").getString("&c&lMaintenance");
    }
    
    public String getSubtitleText() {
        return root.node("timer", "title", "subtitle").getString("&eStarts in {time}");
    }
    
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
    
    // bStats
    public boolean isBStatsEnabled() {
        return root.node("bstats", "enabled").getBoolean(true);
    }
}