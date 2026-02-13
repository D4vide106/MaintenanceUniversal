package me.d4vide106.maintenance.universal;

import me.d4vide106.maintenance.universal.PlatformDetector.Platform;

import java.util.logging.Logger;

/**
 * Universal bootstrap that detects platform and loads appropriate implementation.
 * This class is called by each platform's plugin loader.
 * 
 * @author D4vide106
 * @version 1.0.0
 */
public class UniversalBootstrap {
    
    private static final Logger LOGGER = Logger.getLogger("MaintenanceUniversal");
    private static boolean initialized = false;
    private static Platform detectedPlatform = null;
    
    /**
     * Initializes the plugin for the detected platform.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        printBanner();
        
        // Detect platform
        detectedPlatform = PlatformDetector.detect();
        
        LOGGER.info("════════════════════════════════════════════════════════════");
        LOGGER.info("  Universal JAR - Platform Auto-Detection");
        LOGGER.info("════════════════════════════════════════════════════════════");
        LOGGER.info("  Detected Platform: " + detectedPlatform.getName());
        LOGGER.info(PlatformDetector.getDetailedInfo());
        LOGGER.info("════════════════════════════════════════════════════════════");
        
        if (detectedPlatform == Platform.UNKNOWN) {
            LOGGER.severe("✗ Unsupported platform detected!");
            LOGGER.severe("This plugin requires: Paper, Spigot, Velocity, or BungeeCord");
            return;
        }
        
        LOGGER.info("✅ Platform supported! Loading implementation...");
        initialized = true;
    }
    
    /**
     * Gets the detected platform.
     */
    public static Platform getDetectedPlatform() {
        return detectedPlatform;
    }
    
    /**
     * Checks if the bootstrap has been initialized.
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    private static void printBanner() {
        LOGGER.info("");
        LOGGER.info("  __  __       _       _                                  ");
        LOGGER.info(" |  \\/  | __ _(_)_ __ | |_ ___ _ __   __ _ _ __   ___ ___ ");
        LOGGER.info(" | |\\/| |/ _` | | '_ \\| __/ _ \\ '_ \\ / _` | '_ \\ / __/ _ \\");
        LOGGER.info(" | |  | | (_| | | | | | ||  __/ | | | (_| | | | | (_|  __/");
        LOGGER.info(" |_|  |_|\\__,_|_|_| |_|\\__\\___|_| |_|\\__,_|_| |_|\\___\\___|");
        LOGGER.info("");
        LOGGER.info("  Universal Maintenance Plugin v1.0.0 - UNIVERSAL JAR");
        LOGGER.info("  Author: D4vide106");
        LOGGER.info("  Supports: Paper, Spigot, Velocity, BungeeCord");
        LOGGER.info("");
    }
}
