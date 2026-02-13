package me.d4vide106.maintenance.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Folia scheduler adapter using region-based scheduling.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class FoliaSchedulerAdapter implements SchedulerAdapter {
    
    @Override
    public void runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        GlobalRegionScheduler scheduler = Bukkit.getGlobalRegionScheduler();
        scheduler.run(plugin, scheduledTask -> task.run());
    }
    
    @Override
    public void runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task) {
        AsyncScheduler scheduler = Bukkit.getAsyncScheduler();
        scheduler.runNow(plugin, scheduledTask -> task.run());
    }
    
    @Override
    public void runTaskLater(@NotNull Plugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        GlobalRegionScheduler scheduler = Bukkit.getGlobalRegionScheduler();
        long ticks = unit.toMillis(delay) / 50;
        scheduler.runDelayed(plugin, scheduledTask -> task.run(), ticks);
    }
    
    @Override
    public void runTaskTimer(
        @NotNull Plugin plugin,
        @NotNull Runnable task,
        long delay,
        long period,
        @NotNull TimeUnit unit
    ) {
        GlobalRegionScheduler scheduler = Bukkit.getGlobalRegionScheduler();
        long delayTicks = unit.toMillis(delay) / 50;
        long periodTicks = unit.toMillis(period) / 50;
        scheduler.runAtFixedRate(plugin, scheduledTask -> task.run(), delayTicks, periodTicks);
    }
    
    @Override
    public boolean isFolia() {
        return true;
    }
}