package me.d4vide106.maintenance.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.velocity.util.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Listener for player connections during maintenance.
 */
public class ConnectionListener {
    
    private final ProxyServer proxy;
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    private final WhitelistManager whitelistManager;
    private final DatabaseProvider database;
    
    public ConnectionListener(
        @NotNull ProxyServer proxy,
        @NotNull MaintenanceAPI api,
        @NotNull MaintenanceConfig config,
        @NotNull WhitelistManager whitelistManager,
        @NotNull DatabaseProvider database
    ) {
        this.proxy = proxy;
        this.api = api;
        this.config = config;
        this.whitelistManager = whitelistManager;
        this.database = database;
    }
    
    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(@NotNull LoginEvent event) {
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
        Component kickMessage = ComponentSerializer.parse(config.getKickMessage());
        event.setResult(ResultedEvent.ComponentResult.denied(kickMessage));
        
        // Increment blocked connections
        database.incrementConnectionsBlocked();
    }
    
    @Subscribe(order = PostOrder.NORMAL)
    public void onServerConnect(@NotNull ServerPreConnectEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
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
            Optional<RegisteredServer> fallback = proxy.getServer(fallbackName);
            if (fallback.isPresent() && config.shouldKickToFallback()) {
                // Redirect to maintenance lobby
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(fallback.get()));
                return;
            }
        }
        
        // No fallback or not configured - disconnect
        Component kickMessage = ComponentSerializer.parse(config.getKickMessage());
        player.disconnect(kickMessage);
    }
}