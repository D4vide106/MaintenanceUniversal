package me.d4vide106.maintenance.api.event;

/**
 * Interface for cancellable events.
 * <p>
 * When an event is cancelled, the action that triggered it will be prevented.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Cancellable {
    
    /**
     * Checks if this event is cancelled.
     * 
     * @return true if cancelled
     */
    boolean isCancelled();
    
    /**
     * Sets the cancelled state of this event.
     * 
     * @param cancelled true to cancel, false to allow
     */
    void setCancelled(boolean cancelled);
}