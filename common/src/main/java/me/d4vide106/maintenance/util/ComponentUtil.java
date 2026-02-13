package me.d4vide106.maintenance.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Utility class for text component handling.
 * <p>
 * Provides methods to parse MiniMessage format and legacy color codes.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ComponentUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();
    
    private ComponentUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Parses a string with MiniMessage format.
     * <p>
     * Example: {@code <gradient:red:blue>Hello</gradient>}
     * </p>
     * 
     * @param text the text to parse
     * @return parsed Component
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        return MINI_MESSAGE.deserialize(text);
    }
    
    /**
     * Parses a string with MiniMessage format and placeholders.
     * <p>
     * Example: {@code parse("Hello {player}!", Map.of("player", "Steve"))}
     * </p>
     * 
     * @param text the text to parse
     * @param placeholders placeholder replacements
     * @return parsed Component
     */
    @NotNull
    public static Component parse(@NotNull String text, @NotNull Map<String, String> placeholders) {
        String replaced = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replaced = replaced.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return MINI_MESSAGE.deserialize(replaced);
    }
    
    /**
     * Parses legacy color codes (& format).
     * <p>
     * Example: {@code &c&lError!} → red bold "Error!"
     * </p>
     */
    @NotNull
    public static Component parseLegacy(@NotNull String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }
    
    /**
     * Serializes a Component to legacy format.
     */
    @NotNull
    public static String toLegacy(@NotNull Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }
    
    /**
     * Strips all formatting from a Component.
     */
    @NotNull
    public static String toPlain(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component)
            .replaceAll("§[0-9a-fk-or]", "");
    }
}