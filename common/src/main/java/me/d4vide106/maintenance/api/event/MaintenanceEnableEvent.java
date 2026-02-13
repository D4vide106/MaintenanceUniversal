package me.d4vide106.maintenance.api.event;

import me.d4vide106.maintenance.api.MaintenanceMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Fired when maintenance mode is about to be enabled.
 * <p>
 * This event is cancellable. If cancelled, maintenance will not be enabled.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceEnableEvent extends MaintenanceEvent implements Cancellable {
    
    private final MaintenanceMode mode;
    private final String server;
    private final String reason;
    private final String enabledBy;
    private boolean cancelled;
    
    /**
     * Creates a new maintenance enable event.
     * 
     * @param mode the maintenance mode being enabled
     * @param server the specific server (null for global)
     * @param reason the maintenance reason (can be null)
     * @param enabledBy who is enabling maintenance (can be null for console/system)
     */
    public MaintenanceEnableEvent(
        @NotNull MaintenanceMode mode,
        @Nullable String server,
        @Nullable String reason,
        @Nullable String enabledBy
    ) {
        super(false);
        this.mode = Objects.requireNonNull(mode);
        this.server = server;
        this.reason = reason;
        this.enabledBy = enabledBy;
        this.cancelled = false;
    }
    
    /**
     * Gets the maintenance mode being enabled.
     */
    @NotNull
    public MaintenanceMode getMode() {
        return mode;
    }
    
    /**
     * Gets the specific server for server-specific maintenance.
     * 
     * @return server name, or null for global maintenance
     */
    @Nullable
    public String getServer() {
        return server;
    }
    
    /**
     * Checks if this is global maintenance.
     */
    public boolean isGlobal() {
        return server == null;
    }
    
    /**
     * Gets the maintenance reason.
     */
    @Nullable
    public String getReason() {
        return reason;
    }
    
    /**
     * Gets who is enabling maintenance.
     * 
     * @return player name/UUID, or null for console/system
     */
    @Nullable
    public String getEnabledBy() {
        return enabledBy;
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