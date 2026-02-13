package me.d4vide106.maintenance.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.velocity.MaintenanceAPIImpl;
import me.d4vide106.maintenance.velocity.MaintenanceVelocity;
import me.d4vide106.maintenance.velocity.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main command handler for /maintenance on Velocity.
 */
public class MaintenanceCommand implements SimpleCommand {
    
    private final MaintenanceVelocity plugin;
    private final MaintenanceAPIImpl api;
    
    public MaintenanceCommand(@NotNull MaintenanceVelocity plugin, @NotNull MaintenanceAPIImpl api) {
        this.plugin = plugin;
        this.api = api;
    }
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        
        if (!source.hasPermission("maintenance.admin")) {
            source.sendMessage(Component.text("No permission", NamedTextColor.RED));
            return;
        }
        
        if (args.length == 0) {
            sendHelp(source);
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "enable":
                handleEnable(source, args);
                break;
            case "disable":
                handleDisable(source);
                break;
            case "toggle":
                handleToggle(source);
                break;
            case "status":
            case "info":
                handleStatus(source);
                break;
            case "whitelist":
                handleWhitelist(source, args);
                break;
            case "stats":
                handleStats(source);
                break;
            case "reload":
                handleReload(source);
                break;
            default:
                sendHelp(source);
        }
    }
    
    private void handleEnable(@NotNull CommandSource source, @NotNull String[] args) {
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;
        
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("Maintenance enabled", NamedTextColor.GREEN));
                
                // Kick non-whitelisted players
                if (plugin.getConfig().isKickOnEnable()) {
                    kickPlayers(source);
                }
            } else {
                source.sendMessage(Component.text("Failed to enable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleDisable(@NotNull CommandSource source) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("Maintenance disabled", NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("Failed to disable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleToggle(@NotNull CommandSource source) {
        if (api.isMaintenanceEnabled()) {
            handleDisable(source);
        } else {
            handleEnable(source, new String[]{"enable"});
        }
    }
    
    private void handleStatus(@NotNull CommandSource source) {
        boolean enabled = api.isMaintenanceEnabled();
        MaintenanceMode mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        source.sendMessage(Component.text("=== Maintenance Status ===", NamedTextColor.GOLD));
        source.sendMessage(Component.text("Enabled: ", NamedTextColor.GRAY)
            .append(Component.text(enabled ? "Yes" : "No", enabled ? NamedTextColor.GREEN : NamedTextColor.RED)));
        source.sendMessage(Component.text("Mode: ", NamedTextColor.GRAY)
            .append(Component.text(mode.name(), NamedTextColor.YELLOW)));
        if (reason != null) {
            source.sendMessage(Component.text("Reason: ", NamedTextColor.GRAY)
                .append(Component.text(reason, NamedTextColor.WHITE)));
        }
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            source.sendMessage(Component.text("Timer: ", NamedTextColor.GRAY)
                .append(Component.text(formatDuration(remaining), NamedTextColor.AQUA)));
        }
    }
    
    private void handleWhitelist(@NotNull CommandSource source, @NotNull String[] args) {
        if (args.length < 2) {
            source.sendMessage(Component.text("Usage: /maintenance whitelist <add|remove|list|clear>", NamedTextColor.RED));
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    source.sendMessage(Component.text("Usage: /maintenance whitelist add <player> [reason]", NamedTextColor.RED));
                    return;
                }
                String playerName = args[2];
                String reason = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : null;
                
                plugin.getServer().getPlayer(playerName).ifPresentOrElse(
                    player -> {
                        api.addToWhitelist(player.getUniqueId(), player.getUsername(), reason).thenAccept(success -> {
                            if (success) {
                                source.sendMessage(Component.text("Added " + player.getUsername() + " to whitelist", NamedTextColor.GREEN));
                            }
                        });
                    },
                    () -> source.sendMessage(Component.text("Player not found", NamedTextColor.RED))
                );
                break;
            
            case "remove":
                if (args.length < 3) {
                    source.sendMessage(Component.text("Usage: /maintenance whitelist remove <player>", NamedTextColor.RED));
                    return;
                }
                plugin.getServer().getPlayer(args[2]).ifPresentOrElse(
                    player -> {
                        api.removeFromWhitelist(player.getUniqueId()).thenAccept(success -> {
                            if (success) {
                                source.sendMessage(Component.text("Removed " + player.getUsername() + " from whitelist", NamedTextColor.GREEN));
                            }
                        });
                    },
                    () -> source.sendMessage(Component.text("Player not found", NamedTextColor.RED))
                );
                break;
            
            case "list":
                List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
                source.sendMessage(Component.text("=== Whitelisted Players (" + players.size() + ") ===", NamedTextColor.GOLD));
                players.forEach(p -> {
                    source.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                        .append(Component.text(p.getName(), NamedTextColor.WHITE)));
                });
                break;
            
            case "clear":
                api.clearWhitelist().thenRun(() -> {
                    source.sendMessage(Component.text("Whitelist cleared", NamedTextColor.GREEN));
                });
                break;
        }
    }
    
    private void handleStats(@NotNull CommandSource source) {
        api.getStats().thenAccept(stats -> {
            source.sendMessage(Component.text("=== Maintenance Statistics ===", NamedTextColor.GOLD));
            source.sendMessage(Component.text("Total Sessions: " + stats.getTotalSessions(), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Total Duration: " + formatDuration(stats.getTotalDuration()), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Players Kicked: " + stats.getPlayersKicked(), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Connections Blocked: " + stats.getConnectionsBlocked(), NamedTextColor.GRAY));
        });
    }
    
    private void handleReload(@NotNull CommandSource source) {
        try {
            plugin.getConfig().load();
            source.sendMessage(Component.text("Configuration reloaded successfully", NamedTextColor.GREEN));
        } catch (Exception e) {
            source.sendMessage(Component.text("Failed to reload configuration", NamedTextColor.RED));
            plugin.getLogger().error("Failed to reload config", e);
        }
    }
    
    private void sendHelp(@NotNull CommandSource source) {
        source.sendMessage(Component.text("=== Maintenance Commands ===", NamedTextColor.GOLD));
        source.sendMessage(Component.text("/maintenance enable [reason] - Enable maintenance", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance disable - Disable maintenance", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance toggle - Toggle maintenance", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance status - Show status", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance whitelist <add|remove|list|clear>", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance stats - Show statistics", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance reload - Reload config", NamedTextColor.GRAY));
    }
    
    private void kickPlayers(@NotNull CommandSource source) {
        String kickMsg = plugin.getConfig().getKickMessage();
        Component kickComponent = ComponentUtil.parse(kickMsg);
        
        int kicked = 0;
        for (Player player : plugin.getServer().getAllPlayers()) {
            if (!player.hasPermission("maintenance.bypass") && 
                !api.isWhitelisted(player.getUniqueId())) {
                player.disconnect(kickComponent);
                kicked++;
            }
        }
        
        if (kicked > 0) {
            source.sendMessage(Component.text("Kicked " + kicked + " players", NamedTextColor.YELLOW));
        }
    }
    
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) return (seconds / 3600) + "h";
        if (seconds >= 60) return (seconds / 60) + "m";
        return seconds + "s";
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length == 0 || args.length == 1) {
            return Arrays.asList("enable", "disable", "toggle", "status", "whitelist", "stats", "reload");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            return Arrays.asList("add", "remove", "list", "clear");
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("whitelist") && 
            (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            return plugin.getServer().getAllPlayers().stream()
                .map(Player::getUsername)
                .toList();
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("maintenance.admin");
    }
}