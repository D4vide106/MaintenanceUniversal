package me.d4vide106.maintenance.velocity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for parsing text into Adventure Components.
 * Supports both MiniMessage and legacy color codes.
 */
public class ComponentSerializer {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Parses a string into a Component.
     * Tries MiniMessage first, falls back to legacy codes.
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        if (text.isEmpty()) {
            return Component.empty();
        }
        
        try {
            // Try MiniMessage first
            if (text.contains("<") && text.contains(">")) {
                return MINI_MESSAGE.deserialize(text);
            }
            
            // Fall back to legacy codes
            return LEGACY.deserialize(text);
        } catch (Exception e) {
            // If both fail, return as plain text
            return Component.text(text);
        }
    }
    
    /**
     * Serializes a Component to MiniMessage format.
     */
    @NotNull
    public static String serialize(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }
    
    /**
     * Serializes a Component to legacy format.
     */
    @NotNull
    public static String serializeLegacy(@NotNull Component component) {
        return LEGACY.serialize(component);
    }
}