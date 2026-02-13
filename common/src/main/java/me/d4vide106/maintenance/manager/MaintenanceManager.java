package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.database.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MaintenanceManager {
    
    private final DatabaseProvider database;
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final AtomicReference<MaintenanceMode> mode = new AtomicReference<>(MaintenanceMode.GLOBAL);
    private final AtomicReference<String> reason = new AtomicReference<>(null);
    private long startTime;
    
    public MaintenanceManager(@NotNull DatabaseProvider database) {
        this.database = database;
        loadState();
    }
    
    private void loadState() {
        database.isMaintenanceEnabled().thenAccept(enabled::set);
        database.getMaintenanceMode().thenAccept(mode::set);
        database.getMaintenanceReason().thenAccept(reason::set);
    }
    
    public boolean isEnabled() {
        return enabled.get();
    }
    
    public MaintenanceMode getMode() {
        return mode.get();
    }
    
    @Nullable
    public String getReason() {
        return reason.get();
    }
    
    public CompletableFuture<Void> enable(@NotNull MaintenanceMode mode, @Nullable String reason) {
        this.enabled.set(true);
        this.mode.set(mode);
        this.reason.set(reason);
        this.startTime = System.currentTimeMillis();
        
        return database.setMaintenanceEnabled(true)
            .thenCompose(v -> database.setMaintenanceMode(mode))
            .thenCompose(v -> database.setMaintenanceReason(reason))
            .thenRun(() -> database.incrementTotalSessions());
    }
    
    public CompletableFuture<Void> disable() {
        if (!enabled.get()) {
            return CompletableFuture.completedFuture(null);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        this.enabled.set(false);
        
        return database.setMaintenanceEnabled(false)
            .thenRun(() -> database.addMaintenanceDuration(duration));
    }
}
