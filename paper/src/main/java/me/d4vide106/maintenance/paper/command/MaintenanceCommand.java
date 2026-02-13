package me.d4vide106.maintenance.paper.command;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.config.MaintenanceConfig;
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
import java.util.stream.Collectors;

/**
 * Main command handler for /maintenance.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceCommand implements CommandExecutor, TabCompleter {
    
    private final MaintenancePaper plugin;
    private final MaintenanceConfig config;
    private final MaintenanceAPI api;
    
    public MaintenanceCommand(
        @NotNull MaintenancePaper plugin,
        @NotNull MaintenanceConfig config,
        @NotNull MaintenanceAPI api
    ) {
        this.plugin = plugin;
        this.config = config;
        this.api = api;
    }
    
    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (!sender.hasPermission("maintenance.command")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
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
                handleStatus(sender);
                break;
            
            case "reload":
                handleReload(sender);
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
            
            case "info":
                handleInfo(sender);
                break;
            
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleEnable(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("maintenance.toggle")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;
        
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("✓ Maintenance enabled!").color(NamedTextColor.GREEN));
                if (reason != null) {
                    sender.sendMessage(Component.text("Reason: " + reason).color(NamedTextColor.GRAY));
                }
            } else {
                sender.sendMessage(Component.text("✗ Maintenance is already enabled!").color(NamedTextColor.RED));
            }
        });
    }
    
    private void handleDisable(@NotNull CommandSender sender) {
        if (!sender.hasPermission("maintenance.toggle")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("✓ Maintenance disabled!").color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("✗ Maintenance is already disabled!").color(NamedTextColor.RED));
            }
        });
    }
    
    private void handleToggle(@NotNull CommandSender sender) {
        if (!sender.hasPermission("maintenance.toggle")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        if (api.isMaintenanceEnabled()) {
            handleDisable(sender);
        } else {
            handleEnable(sender, new String[]{"toggle"});
        }
    }
    
    private void handleStatus(@NotNull CommandSender sender) {
        boolean enabled = api.isMaintenanceEnabled();
        
        sender.sendMessage(Component.text("━━━━━━━━━ Maintenance Status ━━━━━━━━━").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Enabled: " + (enabled ? "✓ Yes" : "✗ No"))
            .color(enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
        
        if (enabled) {
            sender.sendMessage(Component.text("Mode: " + api.getMaintenanceMode().name()).color(NamedTextColor.YELLOW));
            
            String reason = api.getMaintenanceReason();
            if (reason != null) {
                sender.sendMessage(Component.text("Reason: " + reason).color(NamedTextColor.GRAY));
            }
        }
        
        sender.sendMessage(Component.text("Whitelisted: " + api.getWhitelistedPlayers().size()).color(NamedTextColor.AQUA));
        
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            sender.sendMessage(Component.text("Timer: " + formatDuration(remaining)).color(NamedTextColor.YELLOW));
        }
        
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
    }
    
    private void handleReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("maintenance.reload")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        try {
            config.load();
            sender.sendMessage(Component.text("✓ Configuration reloaded!").color(NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("✗ Failed to reload configuration!").color(NamedTextColor.RED));
            e.printStackTrace();
        }
    }
    
    private void handleWhitelist(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("maintenance.whitelist")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /maintenance whitelist <add|remove|list|clear>").color(NamedTextColor.RED));
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /maintenance whitelist add <player> [reason]").color(NamedTextColor.RED));
                    return;
                }
                
                String playerName = args[2];
                String reason = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : null;
                
                Player target = Bukkit.getPlayer(playerName);
                if (target != null) {
                    api.addToWhitelist(target.getUniqueId(), target.getName(), reason).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("✓ Added " + target.getName() + " to whitelist!").color(NamedTextColor.GREEN));
                        } else {
                            sender.sendMessage(Component.text("✗ Player is already whitelisted!").color(NamedTextColor.RED));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("✗ Player not found!").color(NamedTextColor.RED));
                }
                break;
            
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /maintenance whitelist remove <player>").color(NamedTextColor.RED));
                    return;
                }
                
                Player removeTarget = Bukkit.getPlayer(args[2]);
                if (removeTarget != null) {
                    api.removeFromWhitelist(removeTarget.getUniqueId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(Component.text("✓ Removed " + removeTarget.getName() + " from whitelist!").color(NamedTextColor.GREEN));
                        } else {
                            sender.sendMessage(Component.text("✗ Player is not whitelisted!").color(NamedTextColor.RED));
                        }
                    });
                } else {
                    sender.sendMessage(Component.text("✗ Player not found!").color(NamedTextColor.RED));
                }
                break;
            
            case "list":
                List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
                sender.sendMessage(Component.text("━━━━━━━━━ Whitelisted Players ━━━━━━━━━").color(NamedTextColor.GOLD));
                
                if (players.isEmpty()) {
                    sender.sendMessage(Component.text("No players whitelisted.").color(NamedTextColor.GRAY));
                } else {
                    for (WhitelistedPlayer player : players) {
                        sender.sendMessage(Component.text("• " + player.getName()).color(NamedTextColor.YELLOW));
                        if (player.getReason() != null) {
                            sender.sendMessage(Component.text("  Reason: " + player.getReason()).color(NamedTextColor.GRAY));
                        }
                    }
                }
                
                sender.sendMessage(Component.text("Total: " + players.size()).color(NamedTextColor.AQUA));
                sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
                break;
            
            case "clear":
                api.clearWhitelist().thenAccept(v -> {
                    sender.sendMessage(Component.text("✓ Whitelist cleared!").color(NamedTextColor.GREEN));
                });
                break;
            
            default:
                sender.sendMessage(Component.text("Usage: /maintenance whitelist <add|remove|list|clear>").color(NamedTextColor.RED));
                break;
        }
    }
    
    private void handleSchedule(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("maintenance.schedule")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /maintenance schedule <delay> <duration>").color(NamedTextColor.RED));
            sender.sendMessage(Component.text("Example: /maintenance schedule 10m 2h").color(NamedTextColor.GRAY));
            return;
        }
        
        try {
            Duration delay = parseDuration(args[1]);
            Duration duration = parseDuration(args[2]);
            
            api.scheduleTimer(delay, duration).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(Component.text("✓ Maintenance scheduled!").color(NamedTextColor.GREEN));
                    sender.sendMessage(Component.text("Start: " + formatDuration(delay)).color(NamedTextColor.YELLOW));
                    sender.sendMessage(Component.text("Duration: " + formatDuration(duration)).color(NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("✗ A timer is already active!").color(NamedTextColor.RED));
                }
            });
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("✗ Invalid time format! Use: 1h, 30m, 45s").color(NamedTextColor.RED));
        }
    }
    
    private void handleTimer(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("maintenance.schedule")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /maintenance timer <cancel|status>").color(NamedTextColor.RED));
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "cancel":
                api.cancelTimer().thenAccept(success -> {
                    if (success) {
                        sender.sendMessage(Component.text("✓ Timer cancelled!").color(NamedTextColor.GREEN));
                    } else {
                        sender.sendMessage(Component.text("✗ No active timer!").color(NamedTextColor.RED));
                    }
                });
                break;
            
            case "status":
                if (api.isTimerActive()) {
                    Duration remaining = api.getRemainingTime();
                    sender.sendMessage(Component.text("Timer active - Remaining: " + formatDuration(remaining)).color(NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("No active timer.").color(NamedTextColor.GRAY));
                }
                break;
            
            default:
                sender.sendMessage(Component.text("Usage: /maintenance timer <cancel|status>").color(NamedTextColor.RED));
                break;
        }
    }
    
    private void handleStats(@NotNull CommandSender sender) {
        if (!sender.hasPermission("maintenance.stats")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return;
        }
        
        api.getStats().thenAccept(stats -> {
            sender.sendMessage(Component.text("━━━━━━━━━ Maintenance Statistics ━━━━━━━━━").color(NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Total Sessions: " + stats.getTotalSessions()).color(NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Total Duration: " + formatDuration(stats.getTotalDuration())).color(NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Players Kicked: " + stats.getPlayersKicked()).color(NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Connections Blocked: " + stats.getConnectionsBlocked()).color(NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Currently Whitelisted: " + stats.getCurrentWhitelisted()).color(NamedTextColor.AQUA));
            sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
        });
    }
    
    private void handleInfo(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("━━━━━━━━━ MaintenanceUniversal ━━━━━━━━━").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Version: " + plugin.getDescription().getVersion()).color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Author: D4vide106").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Platform: Paper/Spigot").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("GitHub: github.com/D4vide106/MaintenanceUniversal").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
    }
    
    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("━━━━━━━━━ Maintenance Commands ━━━━━━━━━").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/maintenance enable [reason]").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance disable").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance toggle").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance status").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance reload").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance whitelist <add|remove|list|clear>").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance schedule <delay> <duration>").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance timer <cancel|status>").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance stats").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/maintenance info").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
    }
    
    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList(
                "enable", "disable", "toggle", "status", "reload",
                "whitelist", "schedule", "timer", "stats", "info"
            ));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("whitelist")) {
                completions.addAll(Arrays.asList("add", "remove", "list", "clear"));
            } else if (args[0].equalsIgnoreCase("timer")) {
                completions.addAll(Arrays.asList("cancel", "status"));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("whitelist") && 
                (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            }
        }
        
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
            .collect(Collectors.toList());
    }
    
    private Duration parseDuration(String input) {
        input = input.toLowerCase().trim();
        
        if (input.endsWith("h")) {
            return Duration.ofHours(Long.parseLong(input.substring(0, input.length() - 1)));
        } else if (input.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(input.substring(0, input.length() - 1)));
        } else if (input.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(input.substring(0, input.length() - 1)));
        } else {
            throw new IllegalArgumentException("Invalid duration format");
        }
    }
    
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}