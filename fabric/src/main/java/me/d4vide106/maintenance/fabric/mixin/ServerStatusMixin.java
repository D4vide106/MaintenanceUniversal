package me.d4vide106.maintenance.fabric.mixin;

import me.d4vide106.maintenance.fabric.MaintenanceFabric;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin to modify server list MOTD during maintenance.
 */
@Mixin(ServerMetadata.class)
public class ServerStatusMixin {
    
    @ModifyVariable(method = "description", at = @At("HEAD"), argsOnly = true)
    private Text modifyDescription(Text original) {
        var api = MaintenanceFabric.getInstance().getApi();
        var config = MaintenanceFabric.getInstance().getConfig();
        
        if (api == null || !api.isMaintenanceEnabled()) {
            return original;
        }
        
        if (!config.isCustomMOTDEnabled()) {
            return original;
        }
        
        // Custom MOTD
        String line1 = config.getMaintenanceMOTDLine1();
        String line2 = config.getMaintenanceMOTDLine2();
        String motd = line1 + "\n" + line2;
        
        return Text.literal(motd);
    }
}
