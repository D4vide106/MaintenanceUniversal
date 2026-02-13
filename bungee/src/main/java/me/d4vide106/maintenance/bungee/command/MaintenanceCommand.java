package me.d4vide106.maintenance.bungee.command;

import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.bungee.MaintenanceBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for /maintenance on BungeeCord.
 */
public class MaintenanceCommand extends Command implements TabExecutor {
    
    private final MaintenanceBungee plugin;
    private final MaintenanceAPI api;
    
    public MaintenanceCommand(@NotNull MaintenanceBungee plugin, @NotNull MaintenanceAPI api) {
        super("maintenance", "maintenance.admin", "mt", "maint");
        this.plugin = plugin;
        this.api = api;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("maintenance.admin")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You don't have permission!"));
            return;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
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
            
            case "stats":
                handleStats(sender);
                break;
            
            case "reload":
                handleReload(sender);
                break;
            
            default:
                sendHelp(sender);
                break;
        }
    }
    
    private void handleEnable(CommandSender sender, String[] args) {
        String reason = null;
        if (args.length > 1) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }
        
        String finalReason = reason;
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Maintenance enabled"));
                if (finalReason != null) {
                    sender.sendMessage(new TextComponent(ChatColor.GRAY + "Reason: " + finalReason));
                }
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "✗ Failed to enable maintenance"));
            }
        });
    }
    
    private void handleDisable(CommandSender sender) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Maintenance disabled"));
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "✗ Failed to disable maintenance"));
            }
        });
    }
    
    private void handleToggle(CommandSender sender) {
        if (api.isMaintenanceEnabled()) {
            handleDisable(sender);
        } else {
            handleEnable(sender, new String[0]);
        }
    }
    
    private void handleStatus(CommandSender sender) {
        boolean enabled = api.isMaintenanceEnabled();
        MaintenanceMode mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "═══ Maintenance Status ═══"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "Enabled: " + 
            (enabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No")));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "Mode: " + ChatColor.YELLOW + mode.name()));
        
        if (reason != null) {
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Reason: " + ChatColor.WHITE + reason));
        }
        
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            if (remaining != null) {
                sender.sendMessage(new TextComponent(ChatColor.GRAY + "Timer: " + 
                    ChatColor.AQUA + formatDuration(remaining)));
            }
        }
    }
    
    private void handleWhitelist(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /maintenance whitelist <add|remove|list|clear>"));
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /maintenance whitelist add <player> [reason]"));
                    return;
                }
                String playerName = args[2];
                String reason = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : null;
                
                ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);
                if (player != null) {
                    api.addToWhitelist(player.getUniqueId(), player.getName(), reason).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Added " + 
                                ChatColor.WHITE + playerName + ChatColor.GREEN + " to whitelist"));
                        }
                    });
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "✗ Player not found: " + playerName));
                }
                break;
            
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /maintenance whitelist remove <player>"));
                    return;
                }
                String removeName = args[2];
                ProxiedPlayer removePlayer = plugin.getProxy().getPlayer(removeName);
                if (removePlayer != null) {
                    api.removeFromWhitelist(removePlayer.getUniqueId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Removed " + 
                                ChatColor.WHITE + removeName + ChatColor.GREEN + " from whitelist"));
                        }
                    });
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "✗ Player not found: " + removeName));
                }
                break;
            
            case "list":
                List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
                sender.sendMessage(new TextComponent(ChatColor.GOLD + "═══ Whitelisted Players (" + players.size() + ") ═══"));
                players.forEach(p -> {
                    sender.sendMessage(new TextComponent(ChatColor.GRAY + "• " + ChatColor.WHITE + p.getName()));
                });
                break;
            
            case "clear":
                api.clearWhitelist().thenRun(() -> {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Whitelist cleared"));
                });
                break;
            
            default:
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /maintenance whitelist <add|remove|list|clear>"));
                break;
        }
    }
    
    private void handleStats(CommandSender sender) {
        api.getStats().thenAccept(stats -> {
            sender.sendMessage(new TextComponent(ChatColor.GOLD + "═══ Maintenance Statistics ═══"));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Total Sessions: " + stats.getTotalSessions()));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Total Duration: " + formatDuration(stats.getTotalDuration())));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Players Kicked: " + stats.getPlayersKicked()));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Connections Blocked: " + stats.getConnectionsBlocked()));
        });
    }
    
    private void handleReload(CommandSender sender) {
        try {
            plugin.getMaintenanceConfig().reload();
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "✓ Configuration reloaded"));
        } catch (Exception e) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "✗ Failed to reload configuration"));
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "═══ Maintenance Commands ═══"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance enable [reason]"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance disable"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance toggle"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance status"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance whitelist <add|remove|list|clear>"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance stats"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/maintenance reload"));
    }
    
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) return (seconds / 3600) + "h";
        if (seconds >= 60) return (seconds / 60) + "m";
        return seconds + "s";
    }
    
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("enable", "disable", "toggle", "status", "whitelist", "stats", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            completions.addAll(Arrays.asList("add", "remove", "list", "clear"));
        }
        
        return completions;
    }
}