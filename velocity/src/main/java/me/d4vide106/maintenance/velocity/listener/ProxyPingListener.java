package me.d4vide106.maintenance.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.velocity.util.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Listener for server list ping events during maintenance.
 */
public class ProxyPingListener {
    
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    
    public ProxyPingListener(@NotNull MaintenanceAPI api, @NotNull MaintenanceConfig config) {
        this.api = api;
        this.config = config;
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onProxyPing(@NotNull ProxyPingEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        if (!config.isCustomMOTDEnabled()) {
            return;
        }
        
        ServerPing originalPing = event.getPing();
        ServerPing.Builder builder = originalPing.asBuilder();
        
        // Custom MOTD
        Component line1 = ComponentSerializer.parse(config.getMaintenanceMOTDLine1());
        Component line2 = ComponentSerializer.parse(config.getMaintenanceMOTDLine2());
        Component motd = line1.append(Component.newline()).append(line2);
        builder.description(motd);
        
        // Custom version
        if (config.isCustomVersionEnabled()) {
            ServerPing.Version version = new ServerPing.Version(
                originalPing.getVersion().getProtocol(),
                config.getMaintenanceVersionText()
            );
            builder.version(version);
        }
        
        // Custom max players
        if (config.isCustomMaxPlayersEnabled()) {
            List<ServerPing.SamplePlayer> sample = originalPing.getPlayers()
                .map(ServerPing.Players::getSample)
                .orElse(Collections.emptyList());
            
            builder.maximumPlayers(config.getMaintenanceMaxPlayers());
            builder.onlinePlayers(0);
            builder.samplePlayers(sample.toArray(new ServerPing.SamplePlayer[0]));
        }
        
        event.setPing(builder.build());
    }
}
