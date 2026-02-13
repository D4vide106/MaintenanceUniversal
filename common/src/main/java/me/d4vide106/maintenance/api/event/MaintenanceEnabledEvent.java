package me.d4vide106.maintenance.api.event;

import me.d4vide106.maintenance.api.MaintenanceMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Fired after maintenance mode has been successfully enabled.
 * <p>
 * This event is not cancellable.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceEnabledEvent extends MaintenanceEvent {
    
    private final MaintenanceMode mode;
    private final String server;
    private final String reason;
    private final String enabledBy;
    
    public MaintenanceEnabledEvent(
        @NotNull MaintenanceMode mode,
        @Nullable String server,
        @Nullable String reason,
        @Nullable String enabledBy
    ) {
        super(true);
        this.mode = Objects.requireNonNull(mode);
        this.server = server;
        this.reason = reason;
        this.enabledBy = enabledBy;
    }
    
    @NotNull
    public MaintenanceMode getMode() {
        return mode;
    }
    
    @Nullable
    public String getServer() {
        return server;
    }
    
    public boolean isGlobal() {
        return server == null;
    }
    
    @Nullable
    public String getReason() {
        return reason;
    }
    
    @Nullable
    public String getEnabledBy() {
        return enabledBy;
    }
}