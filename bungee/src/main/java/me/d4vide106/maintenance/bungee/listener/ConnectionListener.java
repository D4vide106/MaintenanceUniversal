package me.d4vide106.maintenance.bungee.listener;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.bungee.MaintenanceBungee;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.bungee.util.ComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for player connections during maintenance.
 */
public class ConnectionListener implements Listener {
    
    private final MaintenanceBungee plugin;
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    private final WhitelistManager whitelistManager;
    private final DatabaseProvider database;
    
    public ConnectionListener(
        @NotNull MaintenanceBungee plugin,
        @NotNull MaintenanceAPI api,
        @NotNull MaintenanceConfig config,
        @NotNull WhitelistManager whitelistManager,
        @NotNull DatabaseProvider database
    ) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
        this.whitelistManager = whitelistManager;
        this.database = database;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(@NotNull LoginEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        // Check if player has bypass permission (will check after login)
        // BungeeCord doesn't have permission check during login
        
        // Check whitelist
        if (whitelistManager.isWhitelisted(event.getConnection().getUniqueId())) {
            return;
        }
        
        // Block login
        String kickMessage = ComponentSerializer.toLegacy(config.getKickMessage());
        event.setCancelled(true);
        event.setCancelReason(new TextComponent(kickMessage));
        
        // Increment blocked connections
        database.incrementConnectionsBlocked();
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerConnect(@NotNull ServerConnectEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        ProxiedPlayer player = event.getPlayer();
        
        // Allow bypass permission
        if (player.hasPermission("maintenance.bypass")) {
            return;
        }
        
        // Allow whitelisted players
        if (whitelistManager.isWhitelisted(player.getUniqueId())) {
            return;
        }
        
        // Check if there's a fallback server configured
        String fallbackName = config.getFallbackServer();
        if (fallbackName != null && !fallbackName.isEmpty()) {
            ServerInfo fallback = plugin.getProxy().getServerInfo(fallbackName);
            if (fallback != null && config.shouldKickToFallback()) {
                // Redirect to maintenance lobby
                event.setTarget(fallback);
                return;
            }
        }
        
        // No fallback or not configured - disconnect
        String kickMessage = ComponentSerializer.toLegacy(config.getKickMessage());
        player.disconnect(new TextComponent(kickMessage));
    }
}