package me.d4vide106.maintenance.bungee.listener;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.bungee.util.ComponentSerializer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for server list ping events during maintenance.
 */
public class ProxyPingListener implements Listener {
    
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    
    public ProxyPingListener(@NotNull MaintenanceAPI api, @NotNull MaintenanceConfig config) {
        this.api = api;
        this.config = config;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPing(@NotNull ProxyPingEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        if (!config.isCustomMOTDEnabled()) {
            return;
        }
        
        ServerPing ping = event.getResponse();
        ServerPing.Protocol version = ping.getVersion();
        
        // Custom MOTD
        String line1 = config.getMaintenanceMOTDLine1();
        String line2 = config.getMaintenanceMOTDLine2();
        String motd = ComponentSerializer.toLegacy(line1) + "\n" + ComponentSerializer.toLegacy(line2);
        ping.setDescriptionComponent(new TextComponent(motd));
        
        // Custom version
        if (config.isCustomVersionEnabled()) {
            version.setName(config.getMaintenanceVersionText());
        }
        
        // Custom max players
        if (config.isCustomMaxPlayersEnabled()) {
            ServerPing.Players players = ping.getPlayers();
            players.setMax(config.getMaintenanceMaxPlayers());
            players.setOnline(0);
        }
        
        event.setResponse(ping);
    }
}