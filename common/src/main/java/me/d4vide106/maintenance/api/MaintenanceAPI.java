package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Main API interface for Maintenance Universal
 * <p>
 * This API provides thread-safe methods for managing maintenance mode
 * across multiple server platforms (Paper, Velocity, Fabric, Forge).
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * MaintenanceAPI api = MaintenanceAPI.getInstance();
 * api.enableMaintenance().thenAccept(success -> {
 *     if (success) {
 *         api.broadcastNotification("Maintenance mode enabled!");
 *     }
 * });
 * }</pre>
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MaintenanceAPI {
    
    /**
     * Gets the singleton API instance.
     * 
     * @return the API instance
     * @throws IllegalStateException if the API is not initialized
     */
    static MaintenanceAPI getInstance() {
        return MaintenanceProvider.get();
    }
    
    /**
     * Gets the plugin version.
     * 
     * @return version string (e.g., "1.0.0")
     */
    @NotNull String getVersion();
    
    /**
     * Gets the current platform name.
     * 
     * @return platform name ("Paper", "Velocity", "Fabric", "Forge")
     */
    @NotNull String getPlatform();
    
    // ============================================
    // MAINTENANCE STATUS
    // ============================================
    
    /**
     * Checks if maintenance is enabled globally.
     * 
     * @return true if maintenance is active
     */
    boolean isMaintenanceEnabled();
    
    /**
     * Checks if maintenance is enabled for a specific server.
     * 
     * @param server the server name
     * @return true if maintenance is active for this server
     */
    boolean isMaintenanceEnabled(@NotNull String server);
    
    /**
     * Gets the maintenance mode type.
     * 
     * @return the current mode (GLOBAL, SERVER_SPECIFIC, SCHEDULED)
     */
    @NotNull MaintenanceMode getMaintenanceMode();
    
    // ============================================
    // MAINTENANCE CONTROL
    // ============================================
    
    /**
     * Enables maintenance mode globally.
     * <p>
     * This will kick all non-whitelisted players and prevent new connections.
     * </p>
     * 
     * @return CompletableFuture with true if successful
     */
    @NotNull CompletableFuture<Boolean> enableMaintenance();
    
    /**
     * Enables maintenance for a specific server.
     * 
     * @param server the server name
     * @return CompletableFuture with true if successful
     */
    @NotNull CompletableFuture<Boolean> enableMaintenance(@NotNull String server);
    
    /**
     * Enables maintenance with a custom reason.
     * 
     * @param reason the maintenance reason (supports MiniMessage format)
     * @return CompletableFuture with true if successful
     */
    @NotNull CompletableFuture<Boolean> enableMaintenance(@NotNull String reason);
    
    /**
     * Disables maintenance mode globally.
     * 
     * @return CompletableFuture with true if successful
     */
    @NotNull CompletableFuture<Boolean> disableMaintenance();
    
    /**
     * Disables maintenance for a specific server.
     * 
     * @param server the server name
     * @return CompletableFuture with true if successful
     */
    @NotNull CompletableFuture<Boolean> disableMaintenance(@NotNull String server);
    
    // ============================================
    // WHITELIST MANAGEMENT
    // ============================================
    
    /**
     * Checks if a player is whitelisted.
     * 
     * @param uuid the player UUID
     * @return true if whitelisted
     */
    boolean isWhitelisted(@NotNull UUID uuid);
    
    /**
     * Checks if a player is whitelisted by name.
     * 
     * @param name the player name
     * @return true if whitelisted
     */
    boolean isWhitelisted(@NotNull String name);
    
    /**
     * Gets all whitelisted players.
     * 
     * @return immutable list of whitelisted players
     */
    @NotNull List<WhitelistedPlayer> getWhitelistedPlayers();
    
    /**
     * Adds a player to the whitelist.
     * 
     * @param uuid the player UUID
     * @param name the player name
     * @return CompletableFuture with true if added successfully
     */
    @NotNull CompletableFuture<Boolean> addToWhitelist(@NotNull UUID uuid, @NotNull String name);
    
    /**
     * Adds a player to the whitelist with a custom reason.
     * 
     * @param uuid the player UUID
     * @param name the player name
     * @param reason the whitelist reason
     * @return CompletableFuture with true if added successfully
     */
    @NotNull CompletableFuture<Boolean> addToWhitelist(@NotNull UUID uuid, @NotNull String name, @Nullable String reason);
    
    /**
     * Removes a player from the whitelist.
     * 
     * @param uuid the player UUID
     * @return CompletableFuture with true if removed successfully
     */
    @NotNull CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid);
    
    /**
     * Clears the entire whitelist.
     * 
     * @return CompletableFuture with true if cleared successfully
     */
    @NotNull CompletableFuture<Boolean> clearWhitelist();
    
    // ============================================
    // TIMER & SCHEDULING
    // ============================================
    
    /**
     * Schedules a maintenance timer.
     * 
     * @param startDelay delay before maintenance starts
     * @param duration duration of maintenance
     * @return CompletableFuture with true if scheduled successfully
     */
    @NotNull CompletableFuture<Boolean> scheduleTimer(@NotNull Duration startDelay, @NotNull Duration duration);
    
    /**
     * Schedules a maintenance timer with custom intervals for warnings.
     * 
     * @param startDelay delay before maintenance starts
     * @param duration duration of maintenance
     * @param warningIntervals intervals for warning messages (e.g., 10min, 5min, 1min)
     * @return CompletableFuture with true if scheduled successfully
     */
    @NotNull CompletableFuture<Boolean> scheduleTimer(
        @NotNull Duration startDelay, 
        @NotNull Duration duration,
        @NotNull List<Duration> warningIntervals
    );
    
    /**
     * Cancels the current maintenance timer.
     * 
     * @return CompletableFuture with true if cancelled successfully
     */
    @NotNull CompletableFuture<Boolean> cancelTimer();
    
    /**
     * Gets remaining time until maintenance starts or ends.
     * 
     * @return remaining duration, or empty if no timer is active
     */
    @NotNull Optional<Duration> getRemainingTime();
    
    /**
     * Gets the scheduled start time.
     * 
     * @return scheduled start timestamp, or empty if not scheduled
     */
    @NotNull Optional<Long> getScheduledStart();
    
    /**
     * Gets the scheduled end time.
     * 
     * @return scheduled end timestamp, or empty if not scheduled
     */
    @NotNull Optional<Long> getScheduledEnd();
    
    // ============================================
    // NOTIFICATIONS & MESSAGING
    // ============================================
    
    /**
     * Broadcasts a maintenance notification to all online players.
     * <p>
     * Supports MiniMessage formatting:
     * {@code <gradient:red:blue>Maintenance starting!</gradient>}
     * </p>
     * 
     * @param message the message to broadcast
     */
    void broadcastNotification(@NotNull String message);
    
    /**
     * Broadcasts a notification to specific permission holders.
     * 
     * @param message the message to broadcast
     * @param permission the permission node required
     */
    void broadcastNotification(@NotNull String message, @NotNull String permission);
    
    /**
     * Sends a notification to a specific player.
     * 
     * @param uuid the player UUID
     * @param message the message to send
     */
    void sendNotification(@NotNull UUID uuid, @NotNull String message);
    
    // ============================================
    // MOTD & SERVER LIST
    // ============================================
    
    /**
     * Gets the current maintenance MOTD.
     * 
     * @return the MOTD message
     */
    @NotNull String getMaintenanceMOTD();
    
    /**
     * Sets a custom maintenance MOTD.
     * 
     * @param motd the new MOTD (supports MiniMessage)
     * @return CompletableFuture with true if updated successfully
     */
    @NotNull CompletableFuture<Boolean> setMaintenanceMOTD(@NotNull String motd);
    
    /**
     * Gets the maintenance kick message.
     * 
     * @return the kick message
     */
    @NotNull String getKickMessage();
    
    /**
     * Sets a custom kick message.
     * 
     * @param message the new kick message (supports MiniMessage)
     * @return CompletableFuture with true if updated successfully
     */
    @NotNull CompletableFuture<Boolean> setKickMessage(@NotNull String message);
    
    // ============================================
    // STATISTICS & INFO
    // ============================================
    
    /**
     * Gets maintenance statistics.
     * 
     * @return statistics object with counts and durations
     */
    @NotNull MaintenanceStats getStats();
    
    /**
     * Checks if multi-server sync is enabled.
     * 
     * @return true if Redis sync is active
     */
    boolean isSyncEnabled();
    
    /**
     * Gets all connected servers (for proxy platforms).
     * 
     * @return list of server names
     */
    @NotNull List<String> getConnectedServers();
}