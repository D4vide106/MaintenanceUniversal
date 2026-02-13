package me.d4vide106.maintenance.velocity;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Velocity implementation of MaintenanceAPI.
 */
public class MaintenanceAPIImpl implements MaintenanceAPI {
    
    private final MaintenanceVelocity plugin;
    private final MaintenanceConfig config;
    private final MaintenanceManager maintenanceManager;
    private final WhitelistManager whitelistManager;
    private final TimerManager timerManager;
    
    public MaintenanceAPIImpl(
        @NotNull MaintenanceVelocity plugin,
        @NotNull MaintenanceConfig config,
        @NotNull MaintenanceManager maintenanceManager,
        @NotNull WhitelistManager whitelistManager,
        @NotNull TimerManager timerManager
    ) {
        this.plugin = plugin;
        this.config = config;
        this.maintenanceManager = maintenanceManager;
        this.whitelistManager = whitelistManager;
        this.timerManager = timerManager;
    }
    
    @Override
    public boolean isMaintenanceEnabled() {
        return maintenanceManager.isEnabled();
    }
    
    @Override
    @NotNull
    public MaintenanceMode getMaintenanceMode() {
        return maintenanceManager.getMode();
    }
    
    @Override
    @Nullable
    public String getMaintenanceReason() {
        return maintenanceManager.getReason();
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> enableMaintenance(@NotNull MaintenanceMode mode, @Nullable String reason) {
        return maintenanceManager.enable(mode, reason);
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> disableMaintenance() {
        return maintenanceManager.disable();
    }
    
    @Override
    public boolean isWhitelisted(@NotNull UUID uuid) {
        return whitelistManager.isWhitelisted(uuid);
    }
    
    @Override
    @NotNull
    public List<WhitelistedPlayer> getWhitelistedPlayers() {
        return whitelistManager.getWhitelistedPlayers();
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> addToWhitelist(@NotNull UUID uuid, @NotNull String name, @Nullable String reason) {
        return whitelistManager.addToWhitelist(uuid, name, reason, "Console");
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid) {
        return whitelistManager.removeFromWhitelist(uuid);
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> clearWhitelist() {
        return whitelistManager.clearWhitelist();
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> scheduleTimer(@NotNull Duration startDelay, @NotNull Duration duration) {
        // Timer implementation for Velocity
        return CompletableFuture.completedFuture(false); // TODO: Implement
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> cancelTimer() {
        return timerManager.cancel();
    }
    
    @Override
    public boolean isTimerActive() {
        return timerManager.isScheduled();
    }
    
    @Override
    @NotNull
    public Duration getRemainingTime() {
        return timerManager.getRemainingTime().orElse(Duration.ZERO);
    }
    
    @Override
    @NotNull
    public CompletableFuture<MaintenanceStats> getStats() {
        return maintenanceManager.getStats();
    }
    
    @Override
    @NotNull
    public Duration getCurrentDuration() {
        return maintenanceManager.getCurrentDuration();
    }
}