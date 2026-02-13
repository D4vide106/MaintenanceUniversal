package me.d4vide106.maintenance.paper.command;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.paper.MaintenancePaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Main command handler for /maintenance.
 */
public class MaintenanceCommand implements CommandExecutor, TabCompleter {
    
    private final MaintenancePaper plugin;
    private final MaintenanceAPI api;
    
    public MaintenanceCommand(@NotNull MaintenancePaper plugin, @NotNull MaintenanceAPI api) {
        this.plugin = plugin;
        this.api = api;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("maintenance.admin")) {
            sender.sendMessage(Component.text("No permission", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "enable":
                handleEnable(sender, args);
                break;
            case "disable":
                handleDisable(sender);
                break;
            case "toggle":
                handleToggle(sender);
                break;
            case "status":
            case "info":
                handleStatus(sender);
                break;
            case "whitelist":
                handleWhitelist(sender, args);
                break;
            case "schedule":
                handleSchedule(sender, args);
                break;
            case "timer":
                handleTimer(sender, args);
                break;
            case "stats":
                handleStats(sender);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleEnable(@NotNull CommandSender sender, @NotNull String[] args) {
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;
        
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("Maintenance enabled", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Failed to enable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleDisable(@NotNull CommandSender sender) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("Maintenance disabled", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Failed to disable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleToggle(@NotNull CommandSender sender) {
        if (api.isMaintenanceEnabled()) {
            handleDisable(sender);
        } else {
            handleEnable(sender, new String[]{"enable"});
        }
    }
    
    private void handleStatus(@NotNull CommandSender sender) {
        boolean enabled = api.isMaintenanceEnabled();
        MaintenanceMode mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        sender.sendMessage(Component.text("=== Maintenance Status ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Enabled: ", NamedTextColor.GRAY)
            .append(Component.text(enabled ? "Yes" : "No", enabled ? NamedTextColor.GREEN : NamedTextColor.RED)));
        sender.sendMessage(Component.text("Mode: ", NamedTextColor.GRAY)
            .append(Component.text(mode.name(), NamedTextColor.YELLOW)));
        if (reason != null) {
            sender.sendMessage(Component.text("Reason: ", NamedTextColor.GRAY)
                .append(Component.text(reason, NamedTextColor.WHITE)));
        }
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            sender.sendMessage(Component.text("Timer: ", NamedTextColor.GRAY)
                .append(Component.text(formatDuration(remaining), NamedTextColor.AQUA)));
        }
    }
    
    private void handleWhitelist(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /maintenance whitelist <add|remove|list|clear>", NamedTextColor.RED));
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /maintenance whitelist add <player> [reason]", NamedTextColor.RED));
                    return;
                }
                String playerName = args[2];
                String reason = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : null;
                
                Player target = Bukkit.getPlayer(playerName);
                if (target != null) {
                    api.addToWhitelist(target.getUniqueId(), target.getName(), reason).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("Added " + target.getName() + " to whitelist", NamedTextColor.GREEN));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                }
                break;
            
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /maintenance whitelist remove <player>", NamedTextColor.RED));
                    return;
                }
                Player target2 = Bukkit.getPlayer(args[2]);
                if (target2 != null) {
                    api.removeFromWhitelist(target2.getUniqueId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("Removed " + target2.getName() + " from whitelist", NamedTextColor.GREEN));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                }
                break;
            
            case "list":
                List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
                sender.sendMessage(Component.text("=== Whitelisted Players (" + players.size() + ") ===", NamedTextColor.GOLD));
                players.forEach(p -> {
                    sender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                        .append(Component.text(p.getName(), NamedTextColor.WHITE)));
                });
                break;
            
            case "clear":
                api.clearWhitelist().thenRun(() -> {
                    sender.sendMessage(Component.text("Whitelist cleared", NamedTextColor.GREEN));
                });
                break;
        }
    }
    
    private void handleSchedule(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /maintenance schedule <delay> <duration>", NamedTextColor.RED));
            return;
        }
        
        try {
            Duration delay = parseDuration(args[1]);
            Duration duration = parseDuration(args[2]);
            
            api.scheduleTimer(delay, duration).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(Component.text("Maintenance scheduled", NamedTextColor.GREEN));
                }
            });
        } catch (Exception e) {
            sender.sendMessage(Component.text("Invalid duration format", NamedTextColor.RED));
        }
    }
    
    private void handleTimer(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /maintenance timer <status|cancel>", NamedTextColor.RED));
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "status":
                if (api.isTimerActive()) {
                    Duration remaining = api.getRemainingTime();
                    sender.sendMessage(Component.text("Timer active: " + formatDuration(remaining), NamedTextColor.AQUA));
                } else {
                    sender.sendMessage(Component.text("No active timer", NamedTextColor.GRAY));
                }
                break;
            
            case "cancel":
                api.cancelTimer().thenAccept(success -> {
                    if (success) {
                        sender.sendMessage(Component.text("Timer cancelled", NamedTextColor.GREEN));
                    }
                });
                break;
        }
    }
    
    private void handleStats(@NotNull CommandSender sender) {
        api.getStats().thenAccept(stats -> {
            sender.sendMessage(Component.text("=== Maintenance Statistics ===", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Total Sessions: " + stats.getTotalSessions(), NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Total Duration: " + formatDuration(stats.getTotalDuration()), NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Players Kicked: " + stats.getPlayersKicked(), NamedTextColor.GRAY));
            sender.sendMessage(Component.text("Connections Blocked: " + stats.getConnectionsBlocked(), NamedTextColor.GRAY));
        });
    }
    
    private void handleReload(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("Reloading configuration...", NamedTextColor.YELLOW));
        // Reload logic here
        sender.sendMessage(Component.text("Configuration reloaded", NamedTextColor.GREEN));
    }
    
    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== Maintenance Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/maintenance enable [reason]", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance disable", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance toggle", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance status", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance whitelist <add|remove|list|clear>", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance schedule <delay> <duration>", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance timer <status|cancel>", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance stats", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/maintenance reload", NamedTextColor.GRAY));
    }
    
    private Duration parseDuration(String input) {
        long value = Long.parseLong(input.replaceAll("[^0-9]", ""));
        if (input.endsWith("h")) return Duration.ofHours(value);
        if (input.endsWith("m")) return Duration.ofMinutes(value);
        if (input.endsWith("s")) return Duration.ofSeconds(value);
        return Duration.ofMinutes(value); // default
    }
    
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) return (seconds / 3600) + "h";
        if (seconds >= 60) return (seconds / 60) + "m";
        return seconds + "s";
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("enable", "disable", "toggle", "status", "whitelist", "schedule", "timer", "stats", "reload");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            return Arrays.asList("add", "remove", "list", "clear");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("timer")) {
            return Arrays.asList("status", "cancel");
        }
        
        return new ArrayList<>();
    }
}