package me.d4vide106.maintenance.paper.expansion;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;

/**
 * PlaceholderAPI expansion for MaintenanceUniversal.
 * <p>
 * This class would normally extend PlaceholderExpansion, but since
 * PlaceholderAPI is compileOnly dependency, we keep it as regular class
 * and register it reflectively when PlaceholderAPI is present.
 * </p>
 */
public class MaintenancePlaceholderExpansion {
    
    private final MaintenancePaper plugin;
    private final MaintenanceAPI api;
    
    public MaintenancePlaceholderExpansion(@NotNull MaintenancePaper plugin, @NotNull MaintenanceAPI api) {
        this.plugin = plugin;
        this.api = api;
    }
    
    public @NotNull String getIdentifier() {
        return "maintenance";
    }
    
    public @NotNull String getAuthor() {
        return "D4vide106";
    }
    
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }
    
    public boolean persist() {
        return true;
    }
    
    public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String identifier) {
        UUID uuid = player.getUniqueId();
        
        switch (identifier.toLowerCase()) {
            case "status":
                return api.isMaintenanceEnabled() ? "Enabled" : "Disabled";
            
            case "mode":
                return api.getMaintenanceMode().name();
            
            case "reason":
                String reason = api.getMaintenanceReason();
                return reason != null ? reason : "No reason specified";
            
            case "duration":
                return "N/A"; // Would need tracking
            
            case "remaining":
                if (api.isTimerActive()) {
                    return formatDuration(api.getRemainingTime());
                }
                return "No timer active";
            
            case "whitelist_count":
                return String.valueOf(api.getWhitelistedPlayers().size());
            
            case "is_whitelisted":
                return String.valueOf(api.isWhitelisted(uuid));
            
            case "can_bypass":
                return String.valueOf(player.getPlayer() != null && player.getPlayer().hasPermission("maintenance.bypass"));
            
            case "status_colored":
                return api.isMaintenanceEnabled() ? "§cEnabled" : "§aDisabled";
            
            case "status_symbol":
                return api.isMaintenanceEnabled() ? "✗" : "✓";
            
            case "timer_active":
                return String.valueOf(api.isTimerActive());
            
            case "enabled":
                return String.valueOf(api.isMaintenanceEnabled());
            
            default:
                return null;
        }
    }
    
    private String formatDuration(@NotNull Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) {
            return (seconds / 3600) + "h";
        } else if (seconds >= 60) {
            return (seconds / 60) + "m";
        } else {
            return seconds + "s";
        }
    }
}