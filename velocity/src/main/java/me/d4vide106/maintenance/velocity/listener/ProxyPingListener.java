package me.d4vide106.maintenance.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.velocity.MaintenanceAPIImpl;
import me.d4vide106.maintenance.velocity.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for customizing server list ping during maintenance.
 */
public class ProxyPingListener {
    
    private final MaintenanceAPIImpl api;
    private final MaintenanceConfig config;
    
    public ProxyPingListener(
        @NotNull MaintenanceAPIImpl api,
        @NotNull MaintenanceConfig config
    ) {
        this.api = api;
        this.config = config;
    }
    
    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        ServerPing originalPing = event.getPing();
        ServerPing.Builder builder = originalPing.asBuilder();
        
        // Custom MOTD
        if (config.isMotdEnabled()) {
            String motdLine1 = config.getMotdLine1();
            String motdLine2 = config.getMotdLine2();
            
            Component motd;
            if (motdLine2 != null && !motdLine2.isEmpty()) {
                motd = ComponentUtil.parse(motdLine1)
                    .append(Component.newline())
                    .append(ComponentUtil.parse(motdLine2));
            } else {
                motd = ComponentUtil.parse(motdLine1);
            }
            
            builder.description(motd);
        }
        
        // Custom version text
        if (config.isVersionTextEnabled()) {
            String versionText = config.getVersionText();
            ServerPing.Version version = new ServerPing.Version(
                originalPing.getVersion().getProtocol(),
                versionText
            );
            builder.version(version);
        }
        
        // Custom max players
        if (config.isMaxPlayersEnabled()) {
            int maxPlayers = config.getMaxPlayers();
            builder.maximumPlayers(maxPlayers);
        }
        
        event.setPing(builder.build());
    }
}