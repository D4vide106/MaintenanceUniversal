package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Fired when a maintenance timer is scheduled.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceTimerStartEvent extends MaintenanceEvent implements Cancellable {
    
    private final Duration startDelay;
    private final Duration duration;
    private final String scheduledBy;
    private boolean cancelled;
    
    public MaintenanceTimerStartEvent(
        Duration startDelay,
        Duration duration,
        @Nullable String scheduledBy
    ) {
        super(false);
        this.startDelay = Objects.requireNonNull(startDelay);
        this.duration = Objects.requireNonNull(duration);
        this.scheduledBy = scheduledBy;
        this.cancelled = false;
    }
    
    public Duration getStartDelay() {
        return startDelay;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    @Nullable
    public String getScheduledBy() {
        return scheduledBy;
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