package me.d4vide106.maintenance.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.velocity.util.ComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Main command handler for /maintenance on Velocity.
 */
public class MaintenanceCommand {
    
    private final ProxyServer proxy;
    private final MaintenanceAPI api;
    
    public MaintenanceCommand(@NotNull ProxyServer proxy, @NotNull MaintenanceAPI api) {
        this.proxy = proxy;
        this.api = api;
    }
    
    public BrigadierCommand createCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal("maintenance")
            .requires(source -> source.hasPermission("maintenance.admin"))
            .executes(context -> {
                sendHelp(context.getSource());
                return 1;
            })
            // /maintenance enable [reason]
            .then(LiteralArgumentBuilder.<CommandSource>literal("enable")
                .executes(context -> {
                    handleEnable(context.getSource(), null);
                    return 1;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.greedyString())
                    .executes(context -> {
                        String reason = context.getArgument("reason", String.class);
                        handleEnable(context.getSource(), reason);
                        return 1;
                    })
                )
            )
            // /maintenance disable
            .then(LiteralArgumentBuilder.<CommandSource>literal("disable")
                .executes(context -> {
                    handleDisable(context.getSource());
                    return 1;
                })
            )
            // /maintenance toggle
            .then(LiteralArgumentBuilder.<CommandSource>literal("toggle")
                .executes(context -> {
                    handleToggle(context.getSource());
                    return 1;
                })
            )
            // /maintenance status
            .then(LiteralArgumentBuilder.<CommandSource>literal("status")
                .executes(context -> {
                    handleStatus(context.getSource());
                    return 1;
                })
            )
            // /maintenance info (alias for status)
            .then(LiteralArgumentBuilder.<CommandSource>literal("info")
                .executes(context -> {
                    handleStatus(context.getSource());
                    return 1;
                })
            )
            // /maintenance whitelist
            .then(LiteralArgumentBuilder.<CommandSource>literal("whitelist")
                .then(LiteralArgumentBuilder.<CommandSource>literal("add")
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String playerName = context.getArgument("player", String.class);
                            handleWhitelistAdd(context.getSource(), playerName, null);
                            return 1;
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.greedyString())
                            .executes(context -> {
                                String playerName = context.getArgument("player", String.class);
                                String reason = context.getArgument("reason", String.class);
                                handleWhitelistAdd(context.getSource(), playerName, reason);
                                return 1;
                            })
                        )
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("remove")
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String playerName = context.getArgument("player", String.class);
                            handleWhitelistRemove(context.getSource(), playerName);
                            return 1;
                        })
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                    .executes(context -> {
                        handleWhitelistList(context.getSource());
                        return 1;
                    })
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("clear")
                    .executes(context -> {
                        handleWhitelistClear(context.getSource());
                        return 1;
                    })
                )
            )
            // /maintenance stats
            .then(LiteralArgumentBuilder.<CommandSource>literal("stats")
                .executes(context -> {
                    handleStats(context.getSource());
                    return 1;
                })
            )
            // /maintenance reload
            .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(context -> {
                    handleReload(context.getSource());
                    return 1;
                })
            )
            .build();
        
        return new BrigadierCommand(node);
    }
    
    private void handleEnable(@NotNull CommandSource source, String reason) {
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("✓ Maintenance enabled", NamedTextColor.GREEN));
                if (reason != null) {
                    source.sendMessage(Component.text("Reason: " + reason, NamedTextColor.GRAY));
                }
            } else {
                source.sendMessage(Component.text("✗ Failed to enable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleDisable(@NotNull CommandSource source) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                source.sendMessage(Component.text("✓ Maintenance disabled", NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("✗ Failed to disable maintenance", NamedTextColor.RED));
            }
        });
    }
    
    private void handleToggle(@NotNull CommandSource source) {
        if (api.isMaintenanceEnabled()) {
            handleDisable(source);
        } else {
            handleEnable(source, null);
        }
    }
    
    private void handleStatus(@NotNull CommandSource source) {
        boolean enabled = api.isMaintenanceEnabled();
        MaintenanceMode mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        source.sendMessage(Component.text("═══ Maintenance Status ═══", NamedTextColor.GOLD));
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
    
    private void handleWhitelistAdd(@NotNull CommandSource source, @NotNull String playerName, String reason) {
        Optional<Player> player = proxy.getPlayer(playerName);
        if (player.isPresent()) {
            api.addToWhitelist(player.get().getUniqueId(), player.get().getUsername(), reason)
                .thenAccept(success -> {
                    if (success) {
                        source.sendMessage(Component.text("✓ Added ", NamedTextColor.GREEN)
                            .append(Component.text(playerName, NamedTextColor.WHITE))
                            .append(Component.text(" to whitelist", NamedTextColor.GREEN)));
                    }
                });
        } else {
            source.sendMessage(Component.text("✗ Player not found: " + playerName, NamedTextColor.RED));
        }
    }
    
    private void handleWhitelistRemove(@NotNull CommandSource source, @NotNull String playerName) {
        Optional<Player> player = proxy.getPlayer(playerName);
        if (player.isPresent()) {
            api.removeFromWhitelist(player.get().getUniqueId()).thenAccept(success -> {
                if (success) {
                    source.sendMessage(Component.text("✓ Removed ", NamedTextColor.GREEN)
                        .append(Component.text(playerName, NamedTextColor.WHITE))
                        .append(Component.text(" from whitelist", NamedTextColor.GREEN)));
                }
            });
        } else {
            source.sendMessage(Component.text("✗ Player not found: " + playerName, NamedTextColor.RED));
        }
    }
    
    private void handleWhitelistList(@NotNull CommandSource source) {
        List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
        source.sendMessage(Component.text("═══ Whitelisted Players (" + players.size() + ") ═══", NamedTextColor.GOLD));
        players.forEach(p -> {
            source.sendMessage(Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text(p.getName(), NamedTextColor.WHITE)));
        });
    }
    
    private void handleWhitelistClear(@NotNull CommandSource source) {
        api.clearWhitelist().thenRun(() -> {
            source.sendMessage(Component.text("✓ Whitelist cleared", NamedTextColor.GREEN));
        });
    }
    
    private void handleStats(@NotNull CommandSource source) {
        api.getStats().thenAccept(stats -> {
            source.sendMessage(Component.text("═══ Maintenance Statistics ═══", NamedTextColor.GOLD));
            source.sendMessage(Component.text("Total Sessions: " + stats.getTotalSessions(), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Total Duration: " + formatDuration(stats.getTotalDuration()), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Players Kicked: " + stats.getPlayersKicked(), NamedTextColor.GRAY));
            source.sendMessage(Component.text("Connections Blocked: " + stats.getConnectionsBlocked(), NamedTextColor.GRAY));
        });
    }
    
    private void handleReload(@NotNull CommandSource source) {
        source.sendMessage(Component.text("✓ Configuration reloaded", NamedTextColor.GREEN));
    }
    
    private void sendHelp(@NotNull CommandSource source) {
        source.sendMessage(Component.text("═══ Maintenance Commands ═══", NamedTextColor.GOLD));
        source.sendMessage(Component.text("/maintenance enable [reason]", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance disable", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance toggle", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance status", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance whitelist <add|remove|list|clear>", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance stats", NamedTextColor.GRAY));
        source.sendMessage(Component.text("/maintenance reload", NamedTextColor.GRAY));
    }
    
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) return (seconds / 3600) + "h";
        if (seconds >= 60) return (seconds / 60) + "m";
        return seconds + "s";
    }
}