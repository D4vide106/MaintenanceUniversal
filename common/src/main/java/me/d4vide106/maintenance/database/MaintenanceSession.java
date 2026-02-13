package me.d4vide106.maintenance.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a historical maintenance session.
 */
public class MaintenanceSession {
    private final int id;
    private final long startTime;
    private final long endTime;
    private final String mode;
    private final String reason;
    private final String startedBy;
    private final int playersKicked;
    
    public MaintenanceSession(
        int id,
        long startTime,
        long endTime,
        @NotNull String mode,
        @Nullable String reason,
        @Nullable String startedBy,
        int playersKicked
    ) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mode = mode;
        this.reason = reason;
        this.startedBy = startedBy;
        this.playersKicked = playersKicked;
    }
    
    public int getId() {
        return id;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    @NotNull
    public String getMode() {
        return mode;
    }
    
    @Nullable
    public String getReason() {
        return reason;
    }
    
    @Nullable
    public String getStartedBy() {
        return startedBy;
    }
    
    public int getPlayersKicked() {
        return playersKicked;
    }
    
    public long getDuration() {
        return endTime - startTime;
    }
}