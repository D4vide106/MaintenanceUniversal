package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.Nullable;

/**
 * Fired after maintenance mode has been successfully disabled.
 * <p>
 * This event is not cancellable.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceDisabledEvent extends MaintenanceEvent {
    
    private final String server;
    private final String disabledBy;
    private final long durationMillis;
    
    public MaintenanceDisabledEvent(
        @Nullable String server,
        @Nullable String disabledBy,
        long durationMillis
    ) {
        super(true);
        this.server = server;
        this.disabledBy = disabledBy;
        this.durationMillis = durationMillis;
    }
    
    @Nullable
    public String getServer() {
        return server;
    }
    
    public boolean isGlobal() {
        return server == null;
    }
    
    @Nullable
    public String getDisabledBy() {
        return disabledBy;
    }
    
    public long getDurationMillis() {
        return durationMillis;
    }
}