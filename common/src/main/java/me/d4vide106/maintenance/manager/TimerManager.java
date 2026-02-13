package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.redis.RedisManager;
import me.d4vide106.maintenance.redis.RedisMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manager for scheduled maintenance timers.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimerManager {
    
    private final ScheduledExecutorService scheduler;
    private final RedisManager redis;
    private final String serverName;
    
    private ScheduledFuture<?> startTask;
    private ScheduledFuture<?> endTask;
    private Consumer<Integer> warningCallback;
    private Runnable startCallback;
    private Runnable endCallback;
    
    private long scheduledStart = 0;
    private long scheduledEnd = 0;
    
    public TimerManager(
        @Nullable RedisManager redis,
        @NotNull String serverName
    ) {
        this.scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r);
            thread.setName("MaintenanceTimer-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
        this.redis = redis;
        this.serverName = serverName;
    }
    
    /**
     * Schedules a maintenance timer.
     * 
     * @param startDelay delay before maintenance starts
     * @param duration how long maintenance will last
     * @param warnings warning intervals (in seconds before start)
     * @param onWarning callback for warnings (receives seconds remaining)
     * @param onStart callback when maintenance starts
     * @param onEnd callback when maintenance ends
     */
    public CompletableFuture<Boolean> schedule(
        @NotNull Duration startDelay,
        @NotNull Duration duration,
        @NotNull int[] warnings,
        @NotNull Consumer<Integer> onWarning,
        @NotNull Runnable onStart,
        @NotNull Runnable onEnd
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (isScheduled()) {
                return false; // Already scheduled
            }
            
            this.warningCallback = onWarning;
            this.startCallback = onStart;
            this.endCallback = onEnd;
            
            this.scheduledStart = System.currentTimeMillis() + startDelay.toMillis();
            this.scheduledEnd = scheduledStart + duration.toMillis();
            
            // Schedule warning messages
            for (int seconds : warnings) {
                long delay = startDelay.toSeconds() - seconds;
                if (delay > 0) {
                    scheduler.schedule(
                        () -> onWarning.accept(seconds),
                        delay,
                        TimeUnit.SECONDS
                    );
                }
            }
            
            // Schedule maintenance start
            startTask = scheduler.schedule(
                onStart,
                startDelay.toMillis(),
                TimeUnit.MILLISECONDS
            );
            
            // Schedule maintenance end
            endTask = scheduler.schedule(
                onEnd,
                startDelay.toMillis() + duration.toMillis(),
                TimeUnit.MILLISECONDS
            );
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.TIMER_SCHEDULED,
                    serverName
                )
                .set("start", String.valueOf(scheduledStart))
                .set("end", String.valueOf(scheduledEnd));
                redis.publish(msg);
            }
            
            return true;
        });
    }
    
    /**
     * Cancels the active timer.
     */
    public CompletableFuture<Boolean> cancel() {
        return CompletableFuture.supplyAsync(() -> {
            if (!isScheduled()) {
                return false;
            }
            
            if (startTask != null) {
                startTask.cancel(false);
            }
            if (endTask != null) {
                endTask.cancel(false);
            }
            
            scheduledStart = 0;
            scheduledEnd = 0;
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.TIMER_CANCELLED,
                    serverName
                );
                redis.publish(msg);
            }
            
            return true;
        });
    }
    
    /**
     * Checks if a timer is currently scheduled.
     */
    public boolean isScheduled() {
        return scheduledStart > 0 && scheduledEnd > 0;
    }
    
    /**
     * Gets remaining time until maintenance starts.
     */
    @NotNull
    public Optional<Duration> getRemainingTime() {
        if (!isScheduled()) {
            return Optional.empty();
        }
        
        long now = System.currentTimeMillis();
        if (now < scheduledStart) {
            // Not started yet
            return Optional.of(Duration.ofMillis(scheduledStart - now));
        } else if (now < scheduledEnd) {
            // Currently active
            return Optional.of(Duration.ofMillis(scheduledEnd - now));
        } else {
            // Ended
            return Optional.empty();
        }
    }
    
    /**
     * Gets scheduled start timestamp.
     */
    public long getScheduledStart() {
        return scheduledStart;
    }
    
    /**
     * Gets scheduled end timestamp.
     */
    public long getScheduledEnd() {
        return scheduledEnd;
    }
    
    /**
     * Shuts down the timer manager.
     */
    public void shutdown() {
        if (startTask != null) {
            startTask.cancel(false);
        }
        if (endTask != null) {
            endTask.cancel(false);
        }
        scheduler.shutdown();
    }
}