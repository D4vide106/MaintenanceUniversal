package me.d4vide106.maintenance.velocity;

import me.d4vide106.maintenance.api.*;
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
    public @NotNull MaintenanceMode getMaintenanceMode() {
        return maintenanceManager.getMode();
    }
    
    @Override
    public @Nullable String getMaintenanceReason() {
        return maintenanceManager.getReason();
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> enableMaintenance(
        @NotNull MaintenanceMode mode,
        @Nullable String reason
    ) {
        return maintenanceManager.enable(mode, reason)
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> disableMaintenance() {
        return maintenanceManager.disable()
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public boolean isWhitelisted(@NotNull UUID uuid) {
        return whitelistManager.isWhitelisted(uuid);
    }
    
    @Override
    public @NotNull List<WhitelistedPlayer> getWhitelistedPlayers() {
        return whitelistManager.getWhitelistedPlayers();
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> addToWhitelist(
        @NotNull UUID uuid,
        @NotNull String name,
        @Nullable String reason
    ) {
        return whitelistManager.add(uuid, name, reason)
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid) {
        return whitelistManager.remove(uuid)
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public @NotNull CompletableFuture<Void> clearWhitelist() {
        return whitelistManager.clearWhitelist();
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> scheduleTimer(
        @NotNull Duration startDelay,
        @NotNull Duration duration
    ) {
        return timerManager.schedule(startDelay, duration)
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> cancelTimer() {
        return timerManager.cancel()
            .thenApply(v -> true)
            .exceptionally(e -> false);
    }
    
    @Override
    public boolean isTimerActive() {
        return timerManager.isActive();
    }
    
    @Override
    public @NotNull Duration getRemainingTime() {
        return timerManager.getRemainingTime();
    }
    
    @Override
    public @NotNull CompletableFuture<MaintenanceStats> getStats() {
        return plugin.getDatabase().getStats();
    }
}
