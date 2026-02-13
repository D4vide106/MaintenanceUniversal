package me.d4vide106.maintenance.paper.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Unified scheduler interface for Bukkit/Spigot/Paper/Folia.
 * <p>
 * Automatically detects platform and uses appropriate scheduler.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SchedulerAdapter {
    
    /**
     * Runs a task on the main thread (or appropriate thread for Folia).
     */
    void runTask(@NotNull Plugin plugin, @NotNull Runnable task);
    
    /**
     * Runs a task asynchronously.
     */
    void runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task);
    
    /**
     * Runs a delayed task.
     */
    void runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit);
    
    /**
     * Runs a repeating task.
     */
    void runTaskTimer(
        @NotNull Plugin plugin,
        @NotNull Runnable task,
        long delay,
        long period,
        @NotNull TimeUnit unit
    );
    
    /**
     * Checks if running on Folia.
     */
    boolean isFolia();
    
    /**
     * Gets the appropriate scheduler adapter for current platform.
     */
    @NotNull
    static SchedulerAdapter getScheduler() {
        try {
            // Try to load Folia classes
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return new FoliaSchedulerAdapter();
        } catch (ClassNotFoundException e) {
            // Fallback to Bukkit scheduler
            return new BukkitSchedulerAdapter();
        }
    }
}