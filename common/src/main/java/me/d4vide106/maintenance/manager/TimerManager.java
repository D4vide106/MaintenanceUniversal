package me.d4vide106.maintenance.manager;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimerManager {
    
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicLong endTime = new AtomicLong(0);
    private ScheduledFuture<?> currentTask;
    
    public TimerManager() {
    }
    
    public CompletableFuture<Void> schedule(
        @NotNull Duration startDelay,
        @NotNull Duration duration
    ) {
        cancel();
        
        active.set(true);
        endTime.set(System.currentTimeMillis() + startDelay.toMillis() + duration.toMillis());
        
        currentTask = executor.schedule(
            () -> active.set(false),
            startDelay.toMillis() + duration.toMillis(),
            TimeUnit.MILLISECONDS
        );
        
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Void> cancel() {
        active.set(false);
        if (currentTask != null) {
            currentTask.cancel(false);
            currentTask = null;
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public boolean isActive() {
        return active.get();
    }
    
    public Duration getRemainingTime() {
        if (!active.get()) {
            return Duration.ZERO;
        }
        long remaining = endTime.get() - System.currentTimeMillis();
        return remaining > 0 ? Duration.ofMillis(remaining) : Duration.ZERO;
    }
    
    public void shutdown() {
        cancel();
        executor.shutdown();
    }
}
