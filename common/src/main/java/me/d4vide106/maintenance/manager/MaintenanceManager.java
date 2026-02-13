package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.MaintenanceMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MaintenanceManager {
    
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final AtomicReference<MaintenanceMode> mode = new AtomicReference<>(MaintenanceMode.GLOBAL);
    private final AtomicReference<String> reason = new AtomicReference<>(null);
    
    public MaintenanceManager() {
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
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Void> disable() {
        this.enabled.set(false);
        return CompletableFuture.completedFuture(null);
    }
}
