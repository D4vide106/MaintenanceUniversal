package me.d4vide106.maintenance.paper.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.paper.util.ComponentAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Listener for customizing server list MOTD during maintenance.
 */
public class ServerListPingListener implements Listener {
    
    private final MaintenanceAPI api;
    private final MaintenanceConfig config;
    private final File iconFile;
    
    public ServerListPingListener(@NotNull MaintenanceAPI api, @NotNull MaintenanceConfig config, @NotNull File iconFile) {
        this.api = api;
        this.config = config;
        this.iconFile = iconFile;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(@NotNull PaperServerListPingEvent event) {
        if (!api.isMaintenanceEnabled()) {
            return;
        }
        
        // Custom MOTD
        if (config.isCustomMOTDEnabled()) {
            String motdText = config.getMaintenanceMOTD();
            Component motd = ComponentAdapter.parse(motdText);
            event.motd(motd);
        }
        
        // Custom max players
        if (config.isCustomMaxPlayersEnabled()) {
            int maxPlayers = config.getMaintenanceMaxPlayers();
            event.setMaxPlayers(maxPlayers);
        }
        
        // Custom server icon
        if (config.isCustomIconEnabled() && iconFile.exists()) {
            try {
                BufferedImage icon = ImageIO.read(iconFile);
                if (icon != null) {
                    event.setServerIcon(org.bukkit.util.CachedServerIcon.class.cast(icon));
                }
            } catch (IOException ignored) {}
        }
    }
}