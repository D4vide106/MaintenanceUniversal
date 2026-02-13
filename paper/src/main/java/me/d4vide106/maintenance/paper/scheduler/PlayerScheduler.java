package me.d4vide106.maintenance.paper.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Player-specific scheduler for thread-safe player operations.
 * <p>
 * On Folia, schedules tasks on the player's region.
 * On Bukkit, schedules on main thread.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerScheduler {
    
    private static final boolean IS_FOLIA;
    
    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {}
        IS_FOLIA = folia;
    }
    
    /**
     * Runs a task on the player's thread/region.
     */
    public static void run(@NotNull Plugin plugin, @NotNull Player player, @NotNull Runnable task) {
        if (IS_FOLIA) {
            // Use Folia's entity scheduler
            player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            // Use Bukkit scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Runs a delayed task on the player's thread/region.
     */
    public static void runDelayed(
        @NotNull Plugin plugin,
        @NotNull Player player,
        @NotNull Runnable task,
        long delay,
        @NotNull TimeUnit unit
    ) {
        if (IS_FOLIA) {
            long ticks = unit.toMillis(delay) / 50;
            player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, ticks);
        } else {
            long ticks = unit.toMillis(delay) / 50;
            Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
        }
    }
    
    /**
     * Checks if running on Folia.
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }
}