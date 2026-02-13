package me.d4vide106.maintenance.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.forge.MaintenanceForge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.List;

/**
 * Maintenance command for Forge using Brigadier.
 */
public class MaintenanceCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, MaintenanceAPI api) {
        dispatcher.register(Commands.literal("maintenance")
            .requires(source -> source.hasPermission(4))
            .then(Commands.literal("enable")
                .executes(ctx -> enable(ctx, api, null))
                .then(Commands.argument("reason", StringArgumentType.greedyString())
                    .executes(ctx -> enable(ctx, api, StringArgumentType.getString(ctx, "reason")))))
            
            .then(Commands.literal("disable")
                .executes(ctx -> disable(ctx, api)))
            
            .then(Commands.literal("toggle")
                .executes(ctx -> toggle(ctx, api)))
            
            .then(Commands.literal("status")
                .executes(ctx -> status(ctx, api)))
            
            .then(Commands.literal("whitelist")
                .then(Commands.literal("add")
                    .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> whitelistAdd(ctx, api, StringArgumentType.getString(ctx, "player"), null))
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                            .executes(ctx -> whitelistAdd(ctx, api, 
                                StringArgumentType.getString(ctx, "player"),
                                StringArgumentType.getString(ctx, "reason"))))))
                
                .then(Commands.literal("remove")
                    .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> whitelistRemove(ctx, api, StringArgumentType.getString(ctx, "player")))))
                
                .then(Commands.literal("list")
                    .executes(ctx -> whitelistList(ctx, api)))
                
                .then(Commands.literal("clear")
                    .executes(ctx -> whitelistClear(ctx, api))))
            
            .then(Commands.literal("stats")
                .executes(ctx -> stats(ctx, api)))
            
            .then(Commands.literal("reload")
                .executes(ctx -> reload(ctx))));
    }
    
    private static int enable(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api, String reason) {
        api.enableMaintenance(MaintenanceMode.GLOBAL, reason).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Maintenance enabled"), true);
                if (reason != null) {
                    ctx.getSource().sendSuccess(() -> Component.literal("§7Reason: " + reason), false);
                }
            } else {
                ctx.getSource().sendFailure(Component.literal("§c✗ Failed to enable maintenance"));
            }
        });
        return 1;
    }
    
    private static int disable(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        api.disableMaintenance().thenAccept(success -> {
            if (success) {
                ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Maintenance disabled"), true);
            } else {
                ctx.getSource().sendFailure(Component.literal("§c✗ Failed to disable maintenance"));
            }
        });
        return 1;
    }
    
    private static int toggle(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        if (api.isMaintenanceEnabled()) {
            return disable(ctx, api);
        } else {
            return enable(ctx, api, null);
        }
    }
    
    private static int status(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        boolean enabled = api.isMaintenanceEnabled();
        var mode = api.getMaintenanceMode();
        String reason = api.getMaintenanceReason();
        
        ctx.getSource().sendSuccess(() -> Component.literal("§6═══ Maintenance Status ═══"), false);
        ctx.getSource().sendSuccess(() -> Component.literal("§7Enabled: " + 
            (enabled ? "§aYes" : "§cNo")), false);
        ctx.getSource().sendSuccess(() -> Component.literal("§7Mode: §e" + mode.name()), false);
        
        if (reason != null) {
            ctx.getSource().sendSuccess(() -> Component.literal("§7Reason: §f" + reason), false);
        }
        
        if (api.isTimerActive()) {
            Duration remaining = api.getRemainingTime();
            if (remaining != null) {
                ctx.getSource().sendSuccess(() -> Component.literal("§7Timer: §b" + 
                    formatDuration(remaining)), false);
            }
        }
        
        return 1;
    }
    
    private static int whitelistAdd(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api, 
                                     String playerName, String reason) {
        var server = ctx.getSource().getServer();
        var player = server.getPlayerList().getPlayerByName(playerName);
        
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("§c✗ Player not found: " + playerName));
            return 0;
        }
        
        api.addToWhitelist(player.getUUID(), player.getName().getString(), reason).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Added §f" + playerName + 
                    "§a to whitelist"), true);
            }
        });
        
        return 1;
    }
    
    private static int whitelistRemove(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api, 
                                        String playerName) {
        var server = ctx.getSource().getServer();
        var player = server.getPlayerList().getPlayerByName(playerName);
        
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("§c✗ Player not found: " + playerName));
            return 0;
        }
        
        api.removeFromWhitelist(player.getUUID()).thenAccept(success -> {
            if (success) {
                ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Removed §f" + playerName + 
                    "§a from whitelist"), true);
            }
        });
        
        return 1;
    }
    
    private static int whitelistList(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        List<WhitelistedPlayer> players = api.getWhitelistedPlayers();
        ctx.getSource().sendSuccess(() -> Component.literal("§6═══ Whitelisted Players (" + 
            players.size() + ") ═══"), false);
        
        players.forEach(p -> {
            ctx.getSource().sendSuccess(() -> Component.literal("§7• §f" + p.getName()), false);
        });
        
        return 1;
    }
    
    private static int whitelistClear(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        api.clearWhitelist().thenRun(() -> {
            ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Whitelist cleared"), true);
        });
        return 1;
    }
    
    private static int stats(CommandContext<CommandSourceStack> ctx, MaintenanceAPI api) {
        api.getStats().thenAccept(stats -> {
            ctx.getSource().sendSuccess(() -> Component.literal("§6═══ Maintenance Statistics ═══"), false);
            ctx.getSource().sendSuccess(() -> Component.literal("§7Total Sessions: " + stats.getTotalSessions()), false);
            ctx.getSource().sendSuccess(() -> Component.literal("§7Total Duration: " + formatDuration(stats.getTotalDuration())), false);
            ctx.getSource().sendSuccess(() -> Component.literal("§7Players Kicked: " + stats.getPlayersKicked()), false);
            ctx.getSource().sendSuccess(() -> Component.literal("§7Connections Blocked: " + stats.getConnectionsBlocked()), false);
        });
        return 1;
    }
    
    private static int reload(CommandContext<CommandSourceStack> ctx) {
        try {
            MaintenanceForge.getInstance().getConfig().reload();
            ctx.getSource().sendSuccess(() -> Component.literal("§a✓ Configuration reloaded"), true);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c✗ Failed to reload configuration"));
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
