package me.d4vide106.maintenance.paper;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.config.MaintenanceConfig;
import me.d4vide106.maintenance.manager.MaintenanceManager;
import me.d4vide106.maintenance.manager.TimerManager;
import me.d4vide106.maintenance.manager.WhitelistManager;
import me.d4vide106.maintenance.paper.scheduler.PlayerScheduler;
import me.d4vide106.maintenance.paper.scheduler.SchedulerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Paper implementation of MaintenanceAPI with full Folia support.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceAPIImpl implements MaintenanceAPI {
    
    private final MaintenancePaper plugin;
    private final MaintenanceConfig config;
    private final MaintenanceManager maintenanceManager;
    private final WhitelistManager whitelistManager;
    private final TimerManager timerManager;
    private final SchedulerAdapter scheduler;
    
    public MaintenanceAPIImpl(
        @NotNull MaintenancePaper plugin,
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
        this.scheduler = SchedulerAdapter.getScheduler();
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
    public @NotNull CompletableFuture<Boolean> enableMaintenance(@NotNull MaintenanceMode mode, @Nullable String reason) {
        return maintenanceManager.enable(mode, reason).thenApply(success -> {
            if (success && config.shouldKickOnEnable()) {
                kickNonWhitelistedPlayers();
            }
            return success;
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> disableMaintenance() {
        return maintenanceManager.disable();
    }
    
    @Override
    public boolean isWhitelisted(@NotNull UUID uuid) {
        return whitelistManager.isWhitelisted(uuid);
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> addToWhitelist(@NotNull UUID uuid, @NotNull String name, @Nullable String reason) {
        return whitelistManager.addToWhitelist(uuid, name, reason, "CONSOLE");
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid) {
        return whitelistManager.removeFromWhitelist(uuid);
    }
    
    @Override
    public @NotNull List<WhitelistedPlayer> getWhitelistedPlayers() {
        return whitelistManager.getWhitelistedPlayers();
    }
    
    @Override
    public @NotNull CompletableFuture<Void> clearWhitelist() {
        return whitelistManager.clearWhitelist();
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> scheduleTimer(@NotNull Duration startDelay, @NotNull Duration duration) {
        return timerManager.schedule(
            startDelay,
            duration,
            config.getWarningIntervals(),
            seconds -> broadcastWarning(seconds),
            () -> enableMaintenance(MaintenanceMode.SCHEDULED, "Scheduled maintenance").join(),
            () -> disableMaintenance().join()
        );
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> cancelTimer() {
        return timerManager.cancel();
    }
    
    @Override
    public boolean isTimerActive() {
        return timerManager.isScheduled();
    }
    
    @Override
    public @NotNull Duration getRemainingTime() {
        return timerManager.getRemainingTime().orElse(Duration.ZERO);
    }
    
    @Override
    public @NotNull CompletableFuture<MaintenanceStats> getStats() {
        return maintenanceManager.getStats();
    }
    
    @Override
    public void broadcastNotification(@NotNull String message) {
        Component component = Component.text(message);
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("maintenance.notify"))
            .forEach(p -> p.sendMessage(component));
    }
    
    @Override
    public void kickNonWhitelistedPlayers() {
        Component kickMessage = Component.text(config.getKickMessage());
        
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> !p.hasPermission("maintenance.bypass"))
            .filter(p -> !whitelistManager.isWhitelisted(p.getUniqueId()))
            .forEach(p -> kickPlayerThreadSafe(p, kickMessage));
    }
    
    /**
     * Kicks a player in a thread-safe manner (Folia-compatible).
     */
    private void kickPlayerThreadSafe(@NotNull Player player, @NotNull Component message) {
        if (config.getKickDelay() > 0) {
            PlayerScheduler.runDelayed(
                plugin,
                player,
                () -> {
                    if (player.isOnline()) {
                        player.kick(message);
                    }
                },
                config.getKickDelay(),
                TimeUnit.SECONDS
            );
        } else {
            PlayerScheduler.run(plugin, player, () -> player.kick(message));
        }
    }
    
    private void broadcastWarning(int seconds) {
        String message = config.getWarningMessage()
            .replace("{time}", formatTime(seconds));
        
        Component component = Component.text(message);
        
        // Broadcast message
        scheduler.runTask(plugin, () -> Bukkit.broadcast(component));
        
        // Send title if enabled
        if (config.isTitleEnabled()) {
            Component title = Component.text(config.getTitleText());
            Component subtitle = Component.text(config.getSubtitleText().replace("{time}", formatTime(seconds)));
            
            Bukkit.getOnlinePlayers().forEach(p -> 
                PlayerScheduler.run(plugin, p, () ->
                    p.showTitle(net.kyori.adventure.title.Title.title(title, subtitle))
                )
            );
        }
        
        // Play sound if enabled
        if (config.isSoundEnabled()) {
            Bukkit.getOnlinePlayers().forEach(p -> 
                PlayerScheduler.run(plugin, p, () ->
                    p.playSound(
                        p.getLocation(),
                        org.bukkit.Sound.valueOf(config.getSoundType()),
                        config.getSoundVolume(),
                        config.getSoundPitch()
                    )
                )
            );
        }
    }
    
    private String formatTime(int seconds) {
        if (seconds >= 3600) {
            int hours = seconds / 3600;
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else if (seconds >= 60) {
            int minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds > 1 ? "s" : "");
        }
    }
}