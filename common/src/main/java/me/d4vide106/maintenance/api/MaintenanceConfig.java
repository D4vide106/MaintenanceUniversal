package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Public API interface for MaintenanceUniversal configuration.
 * This interface provides read-only access to plugin configuration.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MaintenanceConfig {
    
    /**
     * Gets the kick message shown to players.
     */
    @NotNull
    String getKickMessage();
    
    /**
     * Gets whether custom MOTD is enabled.
     */
    boolean isCustomMOTDEnabled();
    
    /**
     * Gets the maintenance MOTD line 1.
     */
    @NotNull
    String getMaintenanceMOTDLine1();
    
    /**
     * Gets the maintenance MOTD line 2.
     */
    @NotNull
    String getMaintenanceMOTDLine2();
    
    /**
     * Gets whether players should be kicked when maintenance is enabled.
     */
    boolean shouldKickOnEnable();
    
    /**
     * Gets the delay in seconds before kicking players.
     */
    int getKickDelay();
    
    /**
     * Gets whether Redis sync is enabled.
     */
    boolean isRedisEnabled();
    
    /**
     * Gets whether proxy mode is enabled (Velocity).
     */
    boolean isProxyMode();
    
    /**
     * Gets the fallback server name (Velocity).
     * Returns null if not configured.
     */
    @Nullable
    String getFallbackServer();
    
    /**
     * Gets the bypass join message.
     */
    @NotNull
    String getBypassJoinMessage();
    
    /**
     * Gets whether debug mode is enabled.
     */
    boolean isDebugEnabled();
}