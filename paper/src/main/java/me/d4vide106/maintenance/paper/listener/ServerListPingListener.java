package me.d4vide106.maintenance.paper.listener;

import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import me.d4vide106.maintenance.paper.util.ComponentAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Listener for server list ping events (MOTD customization).
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServerListPingListener implements Listener {
    
    private final MaintenancePaper plugin;
    private final MaintenanceConfig config;
    private final MaintenanceManager maintenanceManager;
    private CachedServerIcon maintenanceIcon;
    
    public ServerListPingListener(
        @NotNull MaintenancePaper plugin,
        @NotNull MaintenanceConfig config,
        @NotNull MaintenanceManager maintenanceManager
    ) {
        this.plugin = plugin;
        this.config = config;
        this.maintenanceManager = maintenanceManager;
        
        // Load maintenance icon if exists
        loadMaintenanceIcon();
    }
    
    private void loadMaintenanceIcon() {
        File iconFile = new File(plugin.getDataFolder(), "maintenance-icon.png");
        if (iconFile.exists()) {
            try {
                BufferedImage image = ImageIO.read(iconFile);
                maintenanceIcon = plugin.getServer().loadServerIcon(image);
                plugin.getLogger().info("Loaded maintenance server icon");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load maintenance-icon.png: " + e.getMessage());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if (!maintenanceManager.isEnabled()) {
            return;
        }
        
        // Custom MOTD
        if (config.isCustomMOTDEnabled()) {
            String motd = config.getMaintenanceMOTD()
                .replace("{mode}", maintenanceManager.getMode().name())
                .replace("{reason}", maintenanceManager.getReason() != null ? 
                    maintenanceManager.getReason() : "Maintenance in progress");
            
            Component motdComponent = ComponentAdapter.parse(motd);
            event.motd(motdComponent);
        }
        
        // Custom version text
        if (config.isCustomVersionEnabled()) {
            String versionText = config.getMaintenanceVersionText();
            // Note: Setting version text requires ProtocolLib or Paper API 1.19.3+
            try {
                event.setVersion(versionText);
            } catch (NoSuchMethodError ignored) {
                // Older version doesn't support this
            }
        }
        
        // Custom max players
        if (config.isCustomMaxPlayersEnabled()) {
            event.setMaxPlayers(config.getMaintenanceMaxPlayers());
        }
        
        // Custom server icon
        if (config.isCustomIconEnabled() && maintenanceIcon != null) {
            try {
                event.setServerIcon(maintenanceIcon);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to set server icon: " + e.getMessage());
            }
        }
    }
}