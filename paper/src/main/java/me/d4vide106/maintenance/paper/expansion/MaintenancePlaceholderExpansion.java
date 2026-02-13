package me.d4vide106.maintenance.paper.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * PlaceholderAPI expansion for MaintenanceUniversal.
 * <p>
 * Provides placeholders for other plugins to use.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenancePlaceholderExpansion extends PlaceholderExpansion {
    
    private final MaintenancePaper plugin;
    private final MaintenanceAPI api;
    
    public MaintenancePlaceholderExpansion(@NotNull MaintenancePaper plugin) {
        this.plugin = plugin;
        this.api = MaintenanceAPI.getInstance();
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "maintenance";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "D4vide106";
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params.toLowerCase()) {
            // %maintenance_status%
            case "status":
                return api.isMaintenanceEnabled() ? "Enabled" : "Disabled";
            
            // %maintenance_enabled%
            case "enabled":
                return String.valueOf(api.isMaintenanceEnabled());
            
            // %maintenance_mode%
            case "mode":
                return api.getMaintenanceMode().name();
            
            // %maintenance_reason%
            case "reason":
                String reason = api.getMaintenanceReason();
                return reason != null ? reason : "None";
            
            // %maintenance_duration%
            case "duration":
                if (api.isMaintenanceEnabled()) {
                    long millis = plugin.getMaintenanceManager().getDuration();
                    return formatDuration(Duration.ofMillis(millis));
                }
                return "0s";
            
            // %maintenance_remaining%
            case "remaining":
                if (api.isTimerActive()) {
                    return formatDuration(api.getRemainingTime());
                }
                return "N/A";
            
            // %maintenance_timer_active%
            case "timer_active":
                return String.valueOf(api.isTimerActive());
            
            // %maintenance_whitelist_count%
            case "whitelist_count":
                return String.valueOf(api.getWhitelistedPlayers().size());
            
            // %maintenance_is_whitelisted%
            case "is_whitelisted":
                if (player != null) {
                    return String.valueOf(api.isWhitelisted(player.getUniqueId()));
                }
                return "false";
            
            // %maintenance_can_bypass%
            case "can_bypass":
                if (player != null && player.isOnline()) {
                    return String.valueOf(player.getPlayer().hasPermission("maintenance.bypass"));
                }
                return "false";
            
            // %maintenance_status_colored%
            case "status_colored":
                return api.isMaintenanceEnabled() ? "§aEnabled" : "§cDisabled";
            
            // %maintenance_status_symbol%
            case "status_symbol":
                return api.isMaintenanceEnabled() ? "✓" : "✗";
            
            default:
                return null;
        }
    }
    
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        
        StringBuilder sb = new StringBuilder();
        
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("s");
        }
        
        return sb.toString().trim();
    }
}