package me.d4vide106.maintenance.universal;

import org.jetbrains.annotations.NotNull;

/**
 * Detects the current platform and loads the appropriate implementation.
 * Supports: Paper, Spigot, Purpur, Folia, Velocity, BungeeCord, Waterfall.
 * 
 * @author D4vide106
 * @version 1.0.0
 */
public class PlatformDetector {
    
    /**
     * Detected platform types.
     */
    public enum Platform {
        PAPER("Paper", "io.papermc.paper.configuration.Configuration"),
        SPIGOT("Spigot", "org.spigotmc.SpigotConfig"),
        BUKKIT("Bukkit", "org.bukkit.Bukkit"),
        VELOCITY("Velocity", "com.velocitypowered.api.proxy.ProxyServer"),
        BUNGEE("BungeeCord", "net.md_5.bungee.api.ProxyServer"),
        UNKNOWN("Unknown", null);
        
        private final String name;
        private final String detectionClass;
        
        Platform(String name, String detectionClass) {
            this.name = name;
            this.detectionClass = detectionClass;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDetectionClass() {
            return detectionClass;
        }
    }
    
    /**
     * Detects the current platform.
     */
    @NotNull
    public static Platform detect() {
        // Check Paper first (most specific)
        if (classExists(Platform.PAPER.getDetectionClass())) {
            return Platform.PAPER;
        }
        
        // Check Spigot
        if (classExists(Platform.SPIGOT.getDetectionClass())) {
            return Platform.SPIGOT;
        }
        
        // Check generic Bukkit (CraftBukkit, etc.)
        if (classExists(Platform.BUKKIT.getDetectionClass())) {
            return Platform.BUKKIT;
        }
        
        // Check Velocity
        if (classExists(Platform.VELOCITY.getDetectionClass())) {
            return Platform.VELOCITY;
        }
        
        // Check BungeeCord/Waterfall
        if (classExists(Platform.BUNGEE.getDetectionClass())) {
            return Platform.BUNGEE;
        }
        
        return Platform.UNKNOWN;
    }
    
    /**
     * Checks if a class exists in the classpath.
     */
    private static boolean classExists(@NotNull String className) {
        if (className == null) {
            return false;
        }
        
        try {
            Class.forName(className, false, PlatformDetector.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Gets detailed platform information.
     */
    @NotNull
    public static String getDetailedInfo() {
        Platform platform = detect();
        
        StringBuilder info = new StringBuilder();
        info.append("Platform: ").append(platform.getName()).append("\n");
        
        // Try to get version info
        switch (platform) {
            case PAPER:
            case SPIGOT:
            case BUKKIT:
                try {
                    Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
                    Object server = bukkit.getMethod("getServer").invoke(null);
                    String version = (String) server.getClass().getMethod("getVersion").invoke(server);
                    info.append("Version: ").append(version).append("\n");
                } catch (Exception ignored) {}
                break;
            
            case VELOCITY:
                info.append("Type: Proxy\n");
                break;
            
            case BUNGEE:
                try {
                    Class<?> bungee = Class.forName("net.md_5.bungee.api.ProxyServer");
                    Object proxy = bungee.getMethod("getInstance").invoke(null);
                    String version = (String) proxy.getClass().getMethod("getVersion").invoke(proxy);
                    info.append("Version: ").append(version).append("\n");
                } catch (Exception ignored) {}
                break;
        }
        
        return info.toString();
    }
}
