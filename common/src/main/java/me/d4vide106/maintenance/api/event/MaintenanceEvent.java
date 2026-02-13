package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.NotNull;

/**
 * Base class for all maintenance-related events.
 * <p>
 * Events are fired on the platform's main/async thread depending on the implementation.
 * All events are immutable after creation unless explicitly marked as cancellable.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class MaintenanceEvent {
    
    private final long timestamp;
    private final boolean async;
    
    /**
     * Creates a new maintenance event.
     * 
     * @param async whether this event is fired asynchronously
     */
    protected MaintenanceEvent(boolean async) {
        this.timestamp = System.currentTimeMillis();
        this.async = async;
    }
    
    /**
     * Gets when this event was created.
     * 
     * @return Unix timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Checks if this event is fired asynchronously.
     * 
     * @return true if async
     */
    public boolean isAsync() {
        return async;
    }
    
    /**
     * Gets the event name.
     * 
     * @return simple class name
     */
    @NotNull
    public String getEventName() {
        return getClass().getSimpleName();
    }
}