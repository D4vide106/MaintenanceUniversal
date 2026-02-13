package me.d4vide106.maintenance.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.fabric.MaintenanceFabric;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.List;

/**
 * Maintenance command for Fabric using Brigadier.
 */
public class MaintenanceCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, MaintenanceAPI api) {
        dispatcher.register(CommandManager.literal("maintenance")
            .requires(source -> source.hasPermissionLevel(4))
            .then(CommandManager.literal("enable")
                .executes(ctx -> enable(ctx, api, null))
                .then(CommandManager.argument("reason", StringArgumentType.greedyString())
                    .executes(ctx -> enable(ctx, api, StringArgumentType.getString(ctx, "reason")))))
            
            .then(CommandManager.literal("disable")
                .executes(ctx -> disable(ctx, api)))
            
            .then(CommandManager.literal("toggle")
                .executes(ctx -> toggle(ctx, api)))
            
            .then(CommandManager.literal("status")
                .executes(ctx -> status(ctx, api)))
            
            .then(CommandManager.literal("whitelist")
                .then(CommandManager.literal("add")
                    .then(CommandManager.argument("player", StringArgumentType.word())
                        .executes(ctx -> whitelistAdd(ctx, api, StringArgumentType.getString(ctx, "player"), null))
                        .then(CommandManager.argument("reason", StringArgumentType.greedyString())
                            .executes(ctx -> whitelistAdd(ctx, api, 
                                StringArgumentType.getString(ctx, "player"),
                                StringArgumentType.getString(ctx, "reason"))))))
                
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("player", StringArgumentType.word())
                        .executes(ctx -> whitelistRemove(ctx, api, StringArgumentType.getString(ctx, "player")))))
                
                .then(CommandManager.literal("list")
                    .executes(ctx -> whitelistList(ctx, api)))
                
                .then(CommandManager.literal("clear")
                    .executes(ctx -> whitelistClear(ctx, api))))
            
            .then(CommandManager.literal("stats")
                .executes(ctx -> stats(ctx, api)))
            
            .then(CommandManager.literal("reload")
                .executes(ctx -> reload(ctx))));
    }
    
    private static int enable(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api, String reason) {
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Maintenance enabled"), true);
                if (reason != null) {
                    ctx.getSource().sendFeedback(() -> Text.literal("§7Reason: " + reason), false);
                }
            } else {
                ctx.getSource().sendError(Text.literal("§c✗ Failed to enable maintenance"));
            }
        });
        return 1;
    }
    
    private static int disable(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Maintenance disabled"), true);
            } else {
                ctx.getSource().sendError(Text.literal("§c✗ Failed to disable maintenance"));
            }
        });
        return 1;
    }
    
    private static int toggle(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        if (api.isMaintenanceEnabled()) {
            return disable(ctx, api);
        } else {
            return enable(ctx, api, null);
        }
    }
    
    private static int status(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        boolean enabled = api.isMaintenanceEnabled();
        var mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        ctx.getSource().sendFeedback(() -> Text.literal("§6═══ Maintenance Status ═══"), false);
        ctx.getSource().sendFeedback(() -> Text.literal("§7Enabled: " + 
            (enabled ? "§aYes" : "§cNo")), false);
        ctx.getSource().sendFeedback(() -> Text.literal("§7Mode: §e" + mode.name()), false);
        
        if (reason != null) {
            ctx.getSource().sendFeedback(() -> Text.literal("§7Reason: §f" + reason), false);
        }
        
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            if (remaining != null) {
                ctx.getSource().sendFeedback(() -> Text.literal("§7Timer: §b" + 
                    formatDuration(remaining)), false);
            }
        }
        
        return 1;
    }
    
    private static int whitelistAdd(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api, 
                                     String playerName, String reason) {
        var server = ctx.getSource().getServer();
        var player = server.getPlayerManager().getPlayer(playerName);
        
        if (player == null) {
            ctx.getSource().sendError(Text.literal("§c✗ Player not found: " + playerName));
            return 0;
        }
        
        api.addToWhitelist(player.getUuid(), player.getName().getString(), reason).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Added §f" + playerName + 
                    "§a to whitelist"), true);
            }
        });
        
        return 1;
    }
    
    private static int whitelistRemove(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api, 
                                        String playerName) {
        var server = ctx.getSource().getServer();
        var player = server.getPlayerManager().getPlayer(playerName);
        
        if (player == null) {
            ctx.getSource().sendError(Text.literal("§c✗ Player not found: " + playerName));
            return 0;
        }
        
        api.removeFromWhitelist(player.getUuid()).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Removed §f" + playerName + 
                    "§a from whitelist"), true);
            }
        });
        
        return 1;
    }
    
    private static int whitelistList(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
        ctx.getSource().sendFeedback(() -> Text.literal("§6═══ Whitelisted Players (" + 
            players.size() + ") ═══"), false);
        
        players.forEach(p -> {
            ctx.getSource().sendFeedback(() -> Text.literal("§7• §f" + p.getName()), false);
        });
        
        return 1;
    }
    
    private static int whitelistClear(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        api.clearWhitelist().thenRun(() -> {
            ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Whitelist cleared"), true);
        });
        return 1;
    }
    
    private static int stats(CommandContext<ServerCommandSource> ctx, MaintenanceAPI api) {
        api.getStats().thenAccept(stats -> {
            ctx.getSource().sendFeedback(() -> Text.literal("§6═══ Maintenance Statistics ═══"), false);
            ctx.getSource().sendFeedback(() -> Text.literal("§7Total Sessions: " + stats.getTotalSessions()), false);
            ctx.getSource().sendFeedback(() -> Text.literal("§7Total Duration: " + formatDuration(stats.getTotalDuration())), false);
            ctx.getSource().sendFeedback(() -> Text.literal("§7Players Kicked: " + stats.getPlayersKicked()), false);
            ctx.getSource().sendFeedback(() -> Text.literal("§7Connections Blocked: " + stats.getConnectionsBlocked()), false);
        });
        return 1;
    }
    
    private static int reload(CommandContext<ServerCommandSource> ctx) {
        try {
            MaintenanceFabric.getInstance().getConfig().reload();
            ctx.getSource().sendFeedback(() -> Text.literal("§a✓ Configuration reloaded"), true);
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal("§c✗ Failed to reload configuration"));
        }
        return 1;
    }
    
    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds >= 3600) return (seconds / 3600) + "h";
        if (seconds >= 60) return (seconds / 60) + "m";
        return seconds + "s";
    }
}
