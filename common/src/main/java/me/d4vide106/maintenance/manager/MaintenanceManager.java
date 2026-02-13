package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.database.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Manager for maintenance mode operations.
 */
public class MaintenanceManager {
    
    private final DatabaseProvider database;
    private volatile boolean enabled;
    private volatile MaintenanceMode mode;
    private volatile String reason;
    private volatile long lastStarted;
    private volatile String startedBy;
    private volatile int kickedPlayersCount;
    
    public MaintenanceManager(@NotNull DatabaseProvider database) {
        this.database = database;
        this.enabled = false;
        this.mode = MaintenanceMode.DISABLED;
        this.reason = null;
        this.lastStarted = 0;
        this.startedBy = "CONSOLE";
        this.kickedPlayersCount = 0;
        
        // Load current state from database
        loadState();
    }
    
    private void loadState() {
        database.isMaintenanceEnabled().thenAccept(isEnabled -> {
            this.enabled = isEnabled;
            if (isEnabled) {
                database.getMaintenanceMode().thenAccept(modeStr -> {
                    try {
                        this.mode = MaintenanceMode.valueOf(modeStr);
                    } catch (IllegalArgumentException e) {
                        this.mode = MaintenanceMode.GLOBAL;
                    }
                });
                
                database.getMaintenanceReason().thenAccept(r -> this.reason = r);
            }
        });
    }
    
    /**
     * Enables maintenance mode.
     */
    @NotNull
    public CompletableFuture<Boolean> enable(@NotNull MaintenanceMode mode, @Nullable String reason) {
        return enable(mode, reason, "CONSOLE");
    }
    
    /**
     * Enables maintenance mode with tracking of who started it.
     */
    @NotNull
    public CompletableFuture<Boolean> enable(@NotNull MaintenanceMode mode, @Nullable String reason, @NotNull String startedBy) {
        this.enabled = true;
        this.mode = mode;
        this.reason = reason;
        this.lastStarted = System.currentTimeMillis();
        this.startedBy = startedBy;
        this.kickedPlayersCount = 0;
        
        return database.setMaintenanceEnabled(true)
            .thenCompose(v -> database.setMaintenanceMode(mode.name()))
            .thenCompose(v -> database.setMaintenanceReason(reason))
            .thenCompose(v -> database.setLastStarted(lastStarted))
            .thenCompose(v -> database.incrementSessions())
            .thenApply(v -> true)
            .exceptionally(throwable -> {
                throwable.printStackTrace();
                return false;
            });
    }
    
    /**
     * Disables maintenance mode.
     */
    @NotNull
    public CompletableFuture<Boolean> disable() {
        if (!enabled) {
            return CompletableFuture.completedFuture(true);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - lastStarted;
        
        this.enabled = false;
        this.mode = MaintenanceMode.DISABLED;
        
        return database.setMaintenanceEnabled(false)
            .thenCompose(v -> database.setMaintenanceMode(MaintenanceMode.DISABLED.name()))
            .thenCompose(v -> database.setLastEnded(endTime))
            .thenCompose(v -> database.addDuration(duration))
            .thenCompose(v -> database.incrementPlayersKicked(kickedPlayersCount))
            .thenCompose(v -> database.saveSession(
                lastStarted,
                endTime,
                mode.name(),
                reason,
                startedBy,
                kickedPlayersCount
            ))
            .thenApply(v -> {
                // Reset counters
                kickedPlayersCount = 0;
                return true;
            })
            .exceptionally(throwable -> {
                throwable.printStackTrace();
                return false;
            });
    }
    
    /**
     * Checks if maintenance is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Gets current maintenance mode.
     */
    @NotNull
    public MaintenanceMode getMode() {
        return mode;
    }
    
    /**
     * Gets maintenance reason.
     */
    @Nullable
    public String getReason() {
        return reason;
    }
    
    /**
     * Increments kicked players counter.
     */
    public void incrementKickedPlayers() {
        kickedPlayersCount++;
    }
    
    /**
     * Increments kicked players counter by amount.
     */
    public void incrementKickedPlayers(int amount) {
        kickedPlayersCount += amount;
    }
    
    /**
     * Gets maintenance statistics.
     */
    @NotNull
    public CompletableFuture<MaintenanceStats> getStats() {
        return database.getStats();
    }
    
    /**
     * Gets current session duration.
     */
    @NotNull
    public Duration getCurrentDuration() {
        if (!enabled || lastStarted == 0) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(System.currentTimeMillis() - lastStarted);
    }
}