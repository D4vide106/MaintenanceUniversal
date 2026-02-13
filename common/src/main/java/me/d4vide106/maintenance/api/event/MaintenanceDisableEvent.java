package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.Nullable;

/**
 * Fired when maintenance mode is about to be disabled.
 * <p>
 * This event is cancellable. If cancelled, maintenance will remain enabled.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceDisableEvent extends MaintenanceEvent implements Cancellable {
    
    private final String server;
    private final String disabledBy;
    private final long durationMillis;
    private boolean cancelled;
    
    /**
     * Creates a new maintenance disable event.
     * 
     * @param server the specific server (null for global)
     * @param disabledBy who is disabling maintenance (can be null)
     * @param durationMillis how long maintenance was active (in milliseconds)
     */
    public MaintenanceDisableEvent(
        @Nullable String server,
        @Nullable String disabledBy,
        long durationMillis
    ) {
        super(false);
        this.server = server;
        this.disabledBy = disabledBy;
        this.durationMillis = durationMillis;
        this.cancelled = false;
    }
    
    /**
     * Gets the specific server.
     * 
     * @return server name, or null for global
     */
    @Nullable
    public String getServer() {
        return server;
    }
    
    /**
     * Checks if this is global maintenance being disabled.
     */
    public boolean isGlobal() {
        return server == null;
    }
    
    /**
     * Gets who is disabling maintenance.
     */
    @Nullable
    public String getDisabledBy() {
        return disabledBy;
    }
    
    /**
     * Gets how long maintenance was active.
     * 
     * @return duration in milliseconds
     */
    public long getDurationMillis() {
        return durationMillis;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}