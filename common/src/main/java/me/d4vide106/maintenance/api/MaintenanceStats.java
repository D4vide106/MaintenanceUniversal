package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

/**
 * Statistics and metrics for maintenance sessions.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceStats {
    
    private final int totalSessions;
    private final Duration totalDuration;
    private final long lastStarted;
    private final long lastEnded;
    private final int currentWhitelisted;
    private final int playersKicked;
    private final int connectionsBlocked;
    
    /**
     * Creates a new MaintenanceStats instance.
     */
    public MaintenanceStats(
        int totalSessions,
        @NotNull Duration totalDuration,
        long lastStarted,
        long lastEnded,
        int currentWhitelisted,
        int playersKicked,
        int connectionsBlocked
    ) {
        this.totalSessions = totalSessions;
        this.totalDuration = Objects.requireNonNull(totalDuration);
        this.lastStarted = lastStarted;
        this.lastEnded = lastEnded;
        this.currentWhitelisted = currentWhitelisted;
        this.playersKicked = playersKicked;
        this.connectionsBlocked = connectionsBlocked;
    }
    
    /**
     * Gets total number of maintenance sessions.
     */
    public int getTotalSessions() {
        return totalSessions;
    }
    
    /**
     * Gets total duration of all maintenance sessions combined.
     */
    @NotNull
    public Duration getTotalDuration() {
        return totalDuration;
    }
    
    /**
     * Gets when maintenance was last started.
     * 
     * @return Unix timestamp in milliseconds, or 0 if never started
     */
    public long getLastStarted() {
        return lastStarted;
    }
    
    /**
     * Gets when maintenance was last ended.
     * 
     * @return Unix timestamp in milliseconds, or 0 if never ended
     */
    public long getLastEnded() {
        return lastEnded;
    }
    
    /**
     * Gets current number of whitelisted players.
     */
    public int getCurrentWhitelisted() {
        return currentWhitelisted;
    }
    
    /**
     * Gets total players kicked during current session.
     */
    public int getPlayersKicked() {
        return playersKicked;
    }
    
    /**
     * Gets total connection attempts blocked.
     */
    public int getConnectionsBlocked() {
        return connectionsBlocked;
    }
    
    @Override
    public String toString() {
        return "MaintenanceStats{" +
               "totalSessions=" + totalSessions +
               ", totalDuration=" + totalDuration +
               ", currentWhitelisted=" + currentWhitelisted +
               ", playersKicked=" + playersKicked +
               ", connectionsBlocked=" + connectionsBlocked +
               '}';
    }
}