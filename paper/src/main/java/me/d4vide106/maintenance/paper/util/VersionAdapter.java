package me.d4vide106.maintenance.paper.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for detecting and adapting to different Minecraft versions.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class VersionAdapter {
    
    private static final String VERSION;
    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final int PATCH_VERSION;
    
    static {
        String version = Bukkit.getVersion();
        
        // Extract version number from string like "git-Paper-123 (MC: 1.20.4)"
        if (version.contains("MC: ")) {
            version = version.substring(version.indexOf("MC: ") + 4);
            version = version.substring(0, version.indexOf(")"));
        }
        
        VERSION = version;
        
        String[] parts = version.split("\\.");
        MAJOR_VERSION = parts.length > 0 ? parseInt(parts[0]) : 1;
        MINOR_VERSION = parts.length > 1 ? parseInt(parts[1]) : 0;
        PATCH_VERSION = parts.length > 2 ? parseInt(parts[2]) : 0;
    }
    
    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Gets the full version string (e.g., "1.20.4").
     */
    @NotNull
    public static String getVersion() {
        return VERSION;
    }
    
    /**
     * Gets the major version (e.g., 1).
     */
    public static int getMajorVersion() {
        return MAJOR_VERSION;
    }
    
    /**
     * Gets the minor version (e.g., 20).
     */
    public static int getMinorVersion() {
        return MINOR_VERSION;
    }
    
    /**
     * Gets the patch version (e.g., 4).
     */
    public static int getPatchVersion() {
        return PATCH_VERSION;
    }
    
    /**
     * Checks if running on a specific version or higher.
     */
    public static boolean isAtLeast(int major, int minor) {
        return MAJOR_VERSION > major || 
               (MAJOR_VERSION == major && MINOR_VERSION >= minor);
    }
    
    /**
     * Checks if running on 1.13+.
     * This is the "flattening" update with major API changes.
     */
    public static boolean isModern() {
        return isAtLeast(1, 13);
    }
    
    /**
     * Checks if running on 1.16+.
     * This version introduced Adventure API support.
     */
    public static boolean supportsAdventure() {
        return isAtLeast(1, 16);
    }
    
    /**
     * Checks if running on legacy version (1.7-1.12).
     */
    public static boolean isLegacy() {
        return MINOR_VERSION <= 12;
    }
    
    /**
     * Gets version tier for feature support.
     */
    @NotNull
    public static VersionTier getVersionTier() {
        if (MINOR_VERSION >= 16) {
            return VersionTier.MODERN;
        } else if (MINOR_VERSION >= 13) {
            return VersionTier.STABLE;
        } else {
            return VersionTier.LEGACY;
        }
    }
    
    /**
     * Version tiers for feature support levels.
     */
    public enum VersionTier {
        /** 1.16+ - Full Adventure API support */
        MODERN,
        
        /** 1.13-1.15 - Modern API but limited Adventure support */
        STABLE,
        
        /** 1.7-1.12 - Legacy API, fallback to Bukkit chat */
        LEGACY
    }
    
    /**
     * Prints version information to console.
     */
    public static void printVersionInfo() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  Minecraft Version Detection");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  Version: " + VERSION);
        System.out.println("  Major: " + MAJOR_VERSION);
        System.out.println("  Minor: " + MINOR_VERSION);
        System.out.println("  Patch: " + PATCH_VERSION);
        System.out.println("  Tier: " + getVersionTier());
        System.out.println("  Modern: " + isModern());
        System.out.println("  Adventure: " + supportsAdventure());
        System.out.println("  Legacy: " + isLegacy());
        System.out.println("═══════════════════════════════════════════════════");
    }
}