package me.d4vide106.maintenance.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.velocity.MaintenanceAPIImpl;
import me.d4vide106.maintenance.velocity.MaintenanceVelocity;
import me.d4vide106.maintenance.velocity.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for handling player connections during maintenance.
 */
public class ConnectionListener {
    
    private final MaintenanceVelocity plugin;
    private final MaintenanceAPIImpl api;
    private final MaintenanceConfig config;
    private final WhitelistManager whitelistManager;
    private final DatabaseProvider database;
    
    public ConnectionListener(
        @NotNull MaintenanceVelocity plugin,
        @NotNull MaintenanceAPIImpl api,
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
    
    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Check whitelist
        if (whitelistManager.isWhitelisted(player.getUniqueId())) {
            return;
        }
        
        // Check bypass permission
        if (player.hasPermission("maintenance.bypass")) {
            return;
        }
        
        // Block connection
        String kickMsg = config.getKickMessage();
        Component message = ComponentUtil.parse(kickMsg);
        
        event.setResult(ResultedEvent.ComponentResult.denied(message));
        
        // Increment blocked connections
        database.incrementConnectionsBlocked();
    }
    
    @Subscribe(order = PostOrder.NORMAL)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Notify bypass players
        if (player.hasPermission("maintenance.bypass") || whitelistManager.isWhitelisted(player.getUniqueId())) {
            String message = config.getBypassJoinMessage();
            if (message != null && !message.isEmpty()) {
                player.sendMessage(ComponentUtil.parse(message));
            }
        }
    }
}