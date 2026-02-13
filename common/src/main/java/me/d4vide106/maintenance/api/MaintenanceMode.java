package me.d4vide106.maintenance.api;

/**
 * Represents the different maintenance modes available.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public enum MaintenanceMode {
    
    /**
     * No maintenance is active.
     */
    DISABLED,
    
    /**
     * Global maintenance affecting all servers.
     * <p>
     * All non-whitelisted players are kicked and cannot reconnect.
     * </p>
     */
    GLOBAL,
    
    /**
     * Server-specific maintenance.
     * <p>
     * Only specific servers in a proxy network are in maintenance mode.
     * </p>
     */
    SERVER_SPECIFIC,
    
    /**
     * Scheduled maintenance with automatic start/stop.
     * <p>
     * Maintenance will automatically enable at a scheduled time
     * and disable after a specified duration.
     * </p>
     */
    SCHEDULED,
    
    /**
     * Emergency maintenance mode.
     * <p>
     * Similar to GLOBAL but with priority alerts and no timer.
     * Used for critical issues requiring immediate attention.
     * </p>
     */
    EMERGENCY
}