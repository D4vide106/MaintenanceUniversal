package me.d4vide106.maintenance.velocity.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for parsing text into Adventure Components.
 */
public class ComponentUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Parses text into a Component.
     * Supports both MiniMessage and legacy color codes.
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
            
            // Fallback to legacy
            return LEGACY.deserialize(text);
        } catch (Exception e) {
            // If parsing fails, return as plain text
            return Component.text(text);
        }
    }
    
    /**
     * Parses text with placeholder replacements.
     */
    @NotNull
    public static Component parse(@NotNull String text, @NotNull String placeholder, @NotNull String value) {
        return parse(text.replace(placeholder, value));
    }
}