package me.d4vide106.maintenance.paper.listener;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import me.d4vide106.maintenance.paper.util.ComponentAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for handling player connections during maintenance.
 */
public class ConnectionListener implements Listener {
    
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    private final WhitelistManager whitelistManager;
    private final DatabaseProvider database;
    
    public ConnectionListener(
        @NotNull MaintenancePaper plugin,
        @NotNull MaintenanceAPI api,
        @NotNull MaintenanceConfig config,
        @NotNull WhitelistManager whitelistManager,
        @NotNull DatabaseProvider database
    ) {
        this.api = api;
        this.config = config;
        this.whitelistManager = whitelistManager;
        this.database = database;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        // Check whitelist
        if (whitelistManager.isWhitelisted(event.getUniqueId())) {
            return;
        }
        
        // Block connection
        String kickMsg = config.getKickMessage();
        event.disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            ComponentAdapter.parse(kickMsg)
        );
        
        // Increment blocked connections
        database.incrementConnectionsBlocked();
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(@NotNull PlayerLoginEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Check bypass permission
        if (player.hasPermission("maintenance.bypass")) {
            return;
        }
        
        // Check whitelist
        if (whitelistManager.isWhitelisted(player.getUniqueId())) {
            return;
        }
        
        // Block login
        String kickMsg = config.getKickMessage();
        event.disallow(
            PlayerLoginEvent.Result.KICK_OTHER,
            ComponentAdapter.parse(kickMsg)
        );
        
        // Increment blocked connections
        database.incrementConnectionsBlocked();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Notify bypass players
        if (api.isMaintenanceEnabled() && player.hasPermission("maintenance.bypass")) {
            String message = config.getBypassJoinMessage();
            Component component = ComponentAdapter.parse(message);
            player.sendMessage(component);
        }
        
        // Notify admins about maintenance status
        if (player.hasPermission("maintenance.notify") && api.isMaintenanceEnabled()) {
            player.sendMessage(Component.text("§e[Maintenance] §7Server is in maintenance mode"));
        }
    }
}