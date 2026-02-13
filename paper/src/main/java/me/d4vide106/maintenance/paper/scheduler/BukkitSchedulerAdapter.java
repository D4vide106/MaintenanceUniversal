package me.d4vide106.maintenance.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Bukkit/Spigot/Paper scheduler adapter.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class BukkitSchedulerAdapter implements SchedulerAdapter {
    
    @Override
    public void runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }
    
    @Override
    public void runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
    
    @Override
    public void runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        long ticks = unit.toMillis(delay) / 50; // Convert to ticks (20 ticks = 1 second)
        Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
    }
    
    @Override
    public void runTaskTimer(
        @NotNull Plugin plugin,
        @NotNull Runnable task,
        long delay,
        long period,
        @NotNull TimeUnit unit
    ) {
        long delayTicks = unit.toMillis(delay) / 50;
        long periodTicks = unit.toMillis(period) / 50;
        Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }
    
    @Override
    public boolean isFolia() {
        return false;
    }
}