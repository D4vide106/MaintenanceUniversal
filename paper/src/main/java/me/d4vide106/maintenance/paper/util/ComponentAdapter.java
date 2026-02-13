package me.d4vide106.maintenance.paper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter for converting between different text component formats.
 * <p>
 * Supports MiniMessage, Adventure Components, and Legacy ChatColor.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class ComponentAdapter {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacySection();
    
    /**
     * Parses a MiniMessage string to Adventure Component.
     * <p>
     * Falls back to legacy color codes on older versions.
     * </p>
     */
    @NotNull
    public static Component parse(@NotNull String input) {
        if (VersionAdapter.supportsAdventure()) {
            try {
                return MINI_MESSAGE.deserialize(input);
            } catch (Exception e) {
                // Fallback to legacy if MiniMessage fails
                return parseLegacy(input);
            }
        } else {
            return parseLegacy(input);
        }
    }
    
    /**
     * Parses legacy color codes to Adventure Component.
     */
    @NotNull
    public static Component parseLegacy(@NotNull String input) {
        String colored = ChatColor.translateAlternateColorCodes('&', input);
        return LEGACY_SERIALIZER.deserialize(colored);
    }
    
    /**
     * Converts Adventure Component to legacy string.
     */
    @NotNull
    public static String toLegacy(@NotNull Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }
    
    /**
     * Converts MiniMessage string to legacy string.
     */
    @NotNull
    public static String miniMessageToLegacy(@NotNull String input) {
        Component component = parse(input);
        return toLegacy(component);
    }
    
    /**
     * Strips all formatting from a component.
     */
    @NotNull
    public static String stripFormatting(@NotNull Component component) {
        String legacy = toLegacy(component);
        return ChatColor.stripColor(legacy);
    }
    
    /**
     * Checks if a string contains MiniMessage tags.
     */
    public static boolean isMiniMessage(@NotNull String input) {
        return input.contains("<") && input.contains(">");
    }
}