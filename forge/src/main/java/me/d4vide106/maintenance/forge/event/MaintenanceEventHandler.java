package me.d4vide106.maintenance.forge.event;

import com.mojang.authlib.GameProfile;
import me.d4vide106.maintenance.forge.MaintenanceForge;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
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
        
        // Cast to ServerPlayer
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        
        ServerPlayer player = (ServerPlayer) event.getEntity();
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
        
        // Kick player (use getConnection() method instead of connection field)
        String kickMessage = MaintenanceForge.getInstance().getConfig().getKickMessage();
        player.connection.disconnect(Component.literal(kickMessage));
        
        // Increment players kicked (pass 1 as parameter)
        MaintenanceForge.getInstance().getDatabase().incrementPlayersKicked(1);
    }
}
