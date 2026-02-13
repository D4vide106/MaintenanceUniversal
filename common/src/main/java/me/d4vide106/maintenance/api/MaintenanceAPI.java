package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Main API interface for Maintenance Universal.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MaintenanceAPI {
    
    /**
     * Gets the singleton API instance.
     */
    static MaintenanceAPI getInstance() {
        return MaintenanceProvider.get();
    }
    
    // ============================================
    // MAINTENANCE STATUS
    // ============================================
    
    /**
     * Checks if maintenance is enabled.
     */
    boolean isMaintenanceEnabled();
    
    /**
     * Gets the maintenance mode type.
     */
    @NotNull MaintenanceMode getMaintenanceMode();
    
    /**
     * Gets the maintenance reason.
     */
    @Nullable String getMaintenanceReason();
    
    // ============================================
    // MAINTENANCE CONTROL
    // ============================================
    
    /**
     * Enables maintenance mode.
     */
    @NotNull CompletableFuture<Boolean> enableMaintenance(@NotNull MaintenanceMode mode, @Nullable String reason);
    
    /**
     * Disables maintenance mode.
     */
    @NotNull CompletableFuture<Boolean> disableMaintenance();
    
    // ============================================
    // WHITELIST MANAGEMENT
    // ============================================
    
    /**
     * Checks if a player is whitelisted.
     */
    boolean isWhitelisted(@NotNull UUID uuid);
    
    /**
     * Gets all whitelisted players.
     */
    @NotNull List<WhitelistedPlayer> getWhitelistedPlayers();
    
    /**
     * Adds a player to the whitelist.
     */
    @NotNull CompletableFuture<Boolean> addToWhitelist(@NotNull UUID uuid, @NotNull String name, @Nullable String reason);
    
    /**
     * Removes a player from the whitelist.
     */
    @NotNull CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid);
    
    /**
     * Clears the entire whitelist.
     */
    @NotNull CompletableFuture<Void> clearWhitelist();
    
    // ============================================
    // TIMER & SCHEDULING
    // ============================================
    
    /**
     * Schedules a maintenance timer.
     */
    @NotNull CompletableFuture<Boolean> scheduleTimer(@NotNull Duration startDelay, @NotNull Duration duration);
    
    /**
     * Cancels the current maintenance timer.
     */
    @NotNull CompletableFuture<Boolean> cancelTimer();
    
    /**
     * Checks if timer is active.
     */
    boolean isTimerActive();
    
    /**
     * Gets remaining time until maintenance starts or ends.
     * Returns Duration.ZERO if no timer is active.
     */
    @NotNull Duration getRemainingTime();
    
    // ============================================
    // STATISTICS
    // ============================================
    
    /**
     * Gets maintenance statistics.
     */
    @NotNull CompletableFuture<MaintenanceStats> getStats();
}
