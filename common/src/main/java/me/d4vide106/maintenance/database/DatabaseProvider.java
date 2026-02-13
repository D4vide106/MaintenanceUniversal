package me.d4vide106.maintenance.database;

import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.api.MaintenanceStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Database provider interface for maintenance data storage.
 * <p>
 * All database operations are asynchronous and return CompletableFuture.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DatabaseProvider {
    
    /**
     * Initializes the database connection and creates tables.
     * 
     * @return CompletableFuture that completes when initialization is done
     */
    @NotNull CompletableFuture<Void> initialize();
    
    /**
     * Closes the database connection and releases resources.
     * 
     * @return CompletableFuture that completes when shutdown is done
     */
    @NotNull CompletableFuture<Void> shutdown();
    
    /**
     * Checks if the database is connected and ready.
     */
    boolean isConnected();
    
    // ============================================
    // MAINTENANCE STATUS
    // ============================================
    
    /**
     * Gets the current maintenance status.
     * 
     * @return true if maintenance is enabled
     */
    @NotNull CompletableFuture<Boolean> isMaintenanceEnabled();
    
    /**
     * Sets the maintenance status.
     */
    @NotNull CompletableFuture<Void> setMaintenanceEnabled(boolean enabled);
    
    /**
     * Gets the maintenance mode.
     */
    @NotNull CompletableFuture<String> getMaintenanceMode();
    
    /**
     * Sets the maintenance mode.
     */
    @NotNull CompletableFuture<Void> setMaintenanceMode(@NotNull String mode);
    
    /**
     * Gets the maintenance reason.
     */
    @NotNull CompletableFuture<String> getMaintenanceReason();
    
    /**
     * Sets the maintenance reason.
     */
    @NotNull CompletableFuture<Void> setMaintenanceReason(@Nullable String reason);
    
    // ============================================
    // WHITELIST
    // ============================================
    
    /**
     * Checks if a player is whitelisted.
     */
    @NotNull CompletableFuture<Boolean> isWhitelisted(@NotNull UUID uuid);
    
    /**
     * Gets all whitelisted players.
     */
    @NotNull CompletableFuture<List<WhitelistedPlayer>> getWhitelistedPlayers();
    
    /**
     * Adds a player to the whitelist.
     */
    @NotNull CompletableFuture<Void> addToWhitelist(@NotNull WhitelistedPlayer player);
    
    /**
     * Removes a player from the whitelist.
     */
    @NotNull CompletableFuture<Void> removeFromWhitelist(@NotNull UUID uuid);
    
    /**
     * Clears the entire whitelist.
     */
    @NotNull CompletableFuture<Void> clearWhitelist();
    
    // ============================================
    // STATISTICS
    // ============================================
    
    /**
     * Gets maintenance statistics.
     */
    @NotNull CompletableFuture<MaintenanceStats> getStats();
    
    /**
     * Increments total sessions count.
     */
    @NotNull CompletableFuture<Void> incrementSessions();
    
    /**
     * Adds duration to total maintenance time.
     */
    @NotNull CompletableFuture<Void> addDuration(long milliseconds);
    
    /**
     * Increments players kicked count.
     */
    @NotNull CompletableFuture<Void> incrementPlayersKicked(int count);
    
    /**
     * Increments connections blocked count.
     */
    @NotNull CompletableFuture<Void> incrementConnectionsBlocked();
    
    /**
     * Sets the last started timestamp.
     */
    @NotNull CompletableFuture<Void> setLastStarted(long timestamp);
    
    /**
     * Sets the last ended timestamp.
     */
    @NotNull CompletableFuture<Void> setLastEnded(long timestamp);
    
    // ============================================
    // HISTORY
    // ============================================
    
    /**
     * Saves a maintenance session to history.
     */
    @NotNull CompletableFuture<Void> saveSession(
        long startTime,
        long endTime,
        @NotNull String mode,
        @Nullable String reason,
        @Nullable String startedBy,
        int playersKicked
    );
    
    /**
     * Gets recent maintenance sessions.
     * 
     * @param limit maximum number of sessions to retrieve
     */
    @NotNull CompletableFuture<List<MaintenanceSession>> getRecentSessions(int limit);
    
    // ============================================
    // SCHEDULED MAINTENANCE
    // ============================================
    
    /**
     * Gets scheduled maintenance start time.
     */
    @NotNull CompletableFuture<Long> getScheduledStart();
    
    /**
     * Sets scheduled maintenance start time.
     */
    @NotNull CompletableFuture<Void> setScheduledStart(long timestamp);
    
    /**
     * Gets scheduled maintenance end time.
     */
    @NotNull CompletableFuture<Long> getScheduledEnd();
    
    /**
     * Sets scheduled maintenance end time.
     */
    @NotNull CompletableFuture<Void> setScheduledEnd(long timestamp);
    
    /**
     * Clears scheduled maintenance.
     */
    @NotNull CompletableFuture<Void> clearSchedule();
}