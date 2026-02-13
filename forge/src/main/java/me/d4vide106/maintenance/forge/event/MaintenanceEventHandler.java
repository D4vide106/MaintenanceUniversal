package me.d4vide106.maintenance.forge.event;

import com.mojang.authlib.GameProfile;
import me.d4vide106.maintenance.forge.MaintenanceForge;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStatusPingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handler for maintenance-related events.
 */
@Mod.EventBusSubscriber(modid = MaintenanceForge.MOD_ID)
public class MaintenanceEventHandler {
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        var api = MaintenanceForge.getInstance().getApi();
        
        if (api == null || !api.isMaintenanceEnabled()) {
            return;
        }
        
        var player = event.getEntity();
        MinecraftServer server = player.getServer();
        
        if (server == null) {
            return;
        }
        
        // Check if player has OP (bypass permission)
        GameProfile profile = player.getGameProfile();
        boolean isOp = server.getPlayerList().isOp(profile);
        if (isOp) {
            return;
        }
        
        // Check whitelist
        if (api.isWhitelisted(player.getUUID())) {
            return;
        }
        
        // Kick player
        String kickMessage = MaintenanceForge.getInstance().getConfig().getKickMessage();
        player.connection.disconnect(Component.literal(kickMessage));
        
        // Increment players kicked
        MaintenanceForge.getInstance().getDatabase().incrementPlayersKicked();
    }
    
    @SubscribeEvent
    public static void onServerStatusPing(ServerStatusPingEvent event) {
        var api = MaintenanceForge.getInstance().getApi();
        var config = MaintenanceForge.getInstance().getConfig();
        
        if (api == null || !api.isMaintenanceEnabled()) {
            return;
        }
        
        if (!config.isCustomMOTDEnabled()) {
            return;
        }
        
        // Custom MOTD
        String line1 = config.getMaintenanceMOTDLine1();
        String line2 = config.getMaintenanceMOTDLine2();
        String motd = line1 + "\n" + line2;
        
        event.setMotd(Component.literal(motd));
        
        // Custom max players
        if (config.isCustomMaxPlayersEnabled()) {
            event.setMaxPlayers(config.getMaintenanceMaxPlayers());
        }
    }
}
