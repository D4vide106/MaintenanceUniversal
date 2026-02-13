package me.d4vide106.maintenance.fabric.mixin;

import com.mojang.authlib.GameProfile;
import me.d4vide106.maintenance.fabric.MaintenanceFabric;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to block player login during maintenance.
 */
@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {
    
    @Shadow
    @Final
    MinecraftServer server;
    
    @Shadow
    @Final
    ClientConnection connection;
    
    @Shadow
    GameProfile profile;
    
    @Inject(method = "acceptPlayer", at = @At("HEAD"), cancellable = true)
    private void onAcceptPlayer(CallbackInfo ci) {
        var api = MaintenanceFabric.getInstance().getApi();
        
        if (api == null || !api.isMaintenanceEnabled()) {
            return;
        }
        
        // Check if player has OP (bypass permission)
        boolean isOp = server.getPlayerManager().isOperator(profile);
        if (isOp) {
            return;
        }
        
        // Check whitelist
        if (api.isWhitelisted(profile.getId())) {
            return;
        }
        
        // Block login
        String kickMessage = MaintenanceFabric.getInstance().getConfig().getKickMessage();
        connection.disconnect(Text.literal(kickMessage));
        
        // Increment blocked connections
        MaintenanceFabric.getInstance().getDatabase().incrementConnectionsBlocked();
        
        ci.cancel();
    }
}
