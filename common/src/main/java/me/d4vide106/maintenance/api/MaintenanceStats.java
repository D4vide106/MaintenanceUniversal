package me.d4vide106.maintenance.api;

import java.time.Duration;

public class MaintenanceStats {
    private final int totalSessions;
    private final Duration totalDuration;
    private final int playersKicked;
    private final int connectionsBlocked;
    
    public MaintenanceStats(
        int totalSessions,
        Duration totalDuration,
        int playersKicked,
        int connectionsBlocked
    ) {
        this.totalSessions = totalSessions;
        this.totalDuration = totalDuration;
        this.playersKicked = playersKicked;
        this.connectionsBlocked = connectionsBlocked;
    }
    
    public int getTotalSessions() {
        return totalSessions;
    }
    
    public Duration getTotalDuration() {
        return totalDuration;
    }
    
    public int getPlayersKicked() {
        return playersKicked;
    }
    
    public int getConnectionsBlocked() {
        return connectionsBlocked;
    }
}
