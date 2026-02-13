package me.d4vide106.maintenance.paper.listener;

import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for player connection events during maintenance.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConnectionListener implements Listener {
    
    private final MaintenancePaper plugin;
    private final MaintenanceConfig config;
    private final MaintenanceManager maintenanceManager;
    private final WhitelistManager whitelistManager;
    private final MiniMessage miniMessage;
    
    public ConnectionListener(
        @NotNull MaintenancePaper plugin,
        @NotNull MaintenanceConfig config,
        @NotNull MaintenanceManager maintenanceManager,
        @NotNull WhitelistManager whitelistManager
    ) {
        this.plugin = plugin;
        this.config = config;
        this.maintenanceManager = maintenanceManager;
        this.whitelistManager = whitelistManager;
        this.miniMessage = MiniMessage.miniMessage();
    }
    
    /**
     * Handles async pre-login (earliest point to block connections).
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!maintenanceManager.isEnabled()) {
            return;
        }
        
        // Check if player has bypass permission (requires checking async)
        // Note: We can't check permissions here directly, so we rely on whitelist
        
        if (!whitelistManager.isWhitelisted(event.getUniqueId())) {
            String message = config.getKickMessage()
                .replace("{player}", event.getName());
            
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                miniMessage.deserialize(message)
            );
            
            // Increment blocked connections
            plugin.getDatabase().incrementConnectionsBlocked();
        }
    }
    
    /**
     * Handles player login (can check permissions here).
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!maintenanceManager.isEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Check bypass permission
        if (player.hasPermission("maintenance.bypass")) {
            return;
        }
        
        // Check whitelist
        if (!whitelistManager.isWhitelisted(player.getUniqueId())) {
            String message = config.getKickMessage()
                .replace("{player}", player.getName());
            
            event.disallow(
                PlayerLoginEvent.Result.KICK_OTHER,
                miniMessage.deserialize(message)
            );
            
            plugin.getDatabase().incrementConnectionsBlocked();
        }
    }
    
    /**
     * Handles player join (notify admins if player has bypass).
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!maintenanceManager.isEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Notify player if they have bypass
        if (player.hasPermission("maintenance.bypass") || 
            whitelistManager.isWhitelisted(player.getUniqueId())) {
            
            String message = config.getBypassJoinMessage()
                .replace("{player}", player.getName());
            
            player.sendMessage(miniMessage.deserialize(message));
        }
        
        // Notify admins
        if (player.hasPermission("maintenance.notify")) {
            String mode = maintenanceManager.getMode().name();
            String reason = maintenanceManager.getReason();
            
            String notification = String.format(
                "§7[§6Maintenance§7] §eMode: §f%s §7| §eReason: §f%s",
                mode,
                reason != null ? reason : "None"
            );
            
            player.sendMessage(notification);
        }
    }
}