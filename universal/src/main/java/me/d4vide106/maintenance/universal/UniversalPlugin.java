package me.d4vide106.maintenance.universal;

import me.d4vide106.maintenance.universal.PlatformDetector.Platform;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Universal plugin entry point for Bukkit-based platforms.
 * Auto-detects Paper/Spigot and delegates to appropriate implementation.
 */
public class UniversalPlugin extends JavaPlugin {
    
    private Object platformPlugin;
    
    @Override
    public void onEnable() {
        // Initialize bootstrap
        UniversalBootstrap.initialize();
        
        Platform platform = UniversalBootstrap.getDetectedPlatform();
        
        if (platform == Platform.UNKNOWN) {
            getLogger().severe("Failed to detect platform! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Load appropriate platform implementation
        try {
            switch (platform) {
                case PAPER:
                case SPIGOT:
                case BUKKIT:
                    // Delegate to Paper implementation (works for all Bukkit-based)
                    Class<?> paperClass = Class.forName("me.d4vide106.maintenance.paper.MaintenancePaper");
                    platformPlugin = paperClass.getDeclaredConstructor().newInstance();
                    
                    // Call onEnable via reflection
                    paperClass.getMethod("onEnable").invoke(platformPlugin);
                    
                    getLogger().info("✅ Loaded Paper/Bukkit implementation");
                    break;
                
                default:
                    getLogger().warning("Platform detected but no implementation available: " + platform.getName());
                    break;
            }
        } catch (Exception e) {
            getLogger().severe("Failed to load platform implementation: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        if (platformPlugin != null) {
            try {
                platformPlugin.getClass().getMethod("onDisable").invoke(platformPlugin);
            } catch (Exception e) {
                getLogger().warning("Failed to properly disable platform implementation: " + e.getMessage());
            }
        }
    }
}
