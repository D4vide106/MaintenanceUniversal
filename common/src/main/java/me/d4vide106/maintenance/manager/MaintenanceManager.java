package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.redis.RedisManager;
import me.d4vide106.maintenance.redis.RedisMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Core manager for maintenance mode operations.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceManager {
    
    private final DatabaseProvider database;
    private final RedisManager redis;
    private final String serverName;
    
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final AtomicReference<MaintenanceMode> mode = new AtomicReference<>(MaintenanceMode.DISABLED);
    private final AtomicReference<String> reason = new AtomicReference<>("");
    private long startedAt = 0;
    
    public MaintenanceManager(
        @NotNull DatabaseProvider database,
        @Nullable RedisManager redis,
        @NotNull String serverName
    ) {
        this.database = database;
        this.redis = redis;
        this.serverName = serverName;
    }
    
    /**
     * Initializes the manager and loads state from database.
     */
    public CompletableFuture<Void> initialize() {
        return database.isMaintenanceEnabled().thenAccept(enabled -> {
            this.enabled.set(enabled);
            if (enabled) {
                this.mode.set(MaintenanceMode.GLOBAL);
            }
        });
    }
    
    /**
     * Checks if maintenance is enabled.
     */
    public boolean isEnabled() {
        return enabled.get();
    }
    
    /**
     * Gets the current maintenance mode.
     */
    @NotNull
    public MaintenanceMode getMode() {
        return mode.get();
    }
    
    /**
     * Gets the maintenance reason.
     */
    @Nullable
    public String getReason() {
        return reason.get();
    }
    
    /**
     * Enables maintenance mode.
     */
    public CompletableFuture<Boolean> enable(
        @NotNull MaintenanceMode mode,
        @Nullable String reason
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (enabled.getAndSet(true)) {
                return false; // Already enabled
            }
            
            this.mode.set(mode);
            this.reason.set(reason);
            this.startedAt = System.currentTimeMillis();
            
            // Save to database
            database.setMaintenanceEnabled(true).join();
            database.setMaintenanceMode(mode.name()).join();
            database.setMaintenanceReason(reason).join();
            database.setLastStarted(startedAt).join();
            database.incrementSessions().join();
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.MAINTENANCE_ENABLED,
                    serverName
                )
                .set("mode", mode.name())
                .set("reason", reason);
                redis.publish(msg).join();
            }
            
            return true;
        });
    }
    
    /**
     * Disables maintenance mode.
     */
    public CompletableFuture<Boolean> disable() {
        return CompletableFuture.supplyAsync(() -> {
            if (!enabled.getAndSet(false)) {
                return false; // Already disabled
            }
            
            long duration = System.currentTimeMillis() - startedAt;
            
            // Save to database
            database.setMaintenanceEnabled(false).join();
            database.setLastEnded(System.currentTimeMillis()).join();
            database.addDuration(duration).join();
            
            // Save to history
            database.saveSession(
                startedAt,
                System.currentTimeMillis(),
                mode.get().name(),
                reason.get(),
                null, // TODO: Track who started
                0     // TODO: Track kicked count
            ).join();
            
            // Reset state
            this.mode.set(MaintenanceMode.DISABLED);
            this.reason.set(null);
            this.startedAt = 0;
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.MAINTENANCE_DISABLED,
                    serverName
                )
                .set("duration", String.valueOf(duration));
                redis.publish(msg).join();
            }
            
            return true;
        });
    }
    
    /**
     * Gets maintenance statistics.
     */
    public CompletableFuture<MaintenanceStats> getStats() {
        return database.getStats();
    }
    
    /**
     * Gets how long maintenance has been active (in milliseconds).
     */
    public long getDuration() {
        if (!enabled.get() || startedAt == 0) {
            return 0;
        }
        return System.currentTimeMillis() - startedAt;
    }
}