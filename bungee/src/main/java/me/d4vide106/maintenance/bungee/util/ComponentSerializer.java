package me.d4vide106.maintenance.bungee.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for parsing MiniMessage to legacy BungeeCord format.
 */
public class ComponentSerializer {
    
    private static final Pattern MINI_MESSAGE_PATTERN = Pattern.compile("<([^>]+)>");
    
    /**
     * Converts MiniMessage format to legacy BungeeCord format.
     */
    @NotNull
    public static String toLegacy(@NotNull String text) {
        if (text.isEmpty()) {
            return "";
        }
        
        // Try to convert MiniMessage tags to legacy codes
        String result = text;
        
        // Color tags
        result = result.replaceAll("<black>", ChatColor.BLACK + "");
        result = result.replaceAll("<dark_blue>", ChatColor.DARK_BLUE + "");
        result = result.replaceAll("<dark_green>", ChatColor.DARK_GREEN + "");
        result = result.replaceAll("<dark_aqua>", ChatColor.DARK_AQUA + "");
        result = result.replaceAll("<dark_red>", ChatColor.DARK_RED + "");
        result = result.replaceAll("<dark_purple>", ChatColor.DARK_PURPLE + "");
        result = result.replaceAll("<gold>", ChatColor.GOLD + "");
        result = result.replaceAll("<gray>", ChatColor.GRAY + "");
        result = result.replaceAll("<dark_gray>", ChatColor.DARK_GRAY + "");
        result = result.replaceAll("<blue>", ChatColor.BLUE + "");
        result = result.replaceAll("<green>", ChatColor.GREEN + "");
        result = result.replaceAll("<aqua>", ChatColor.AQUA + "");
        result = result.replaceAll("<red>", ChatColor.RED + "");
        result = result.replaceAll("<light_purple>", ChatColor.LIGHT_PURPLE + "");
        result = result.replaceAll("<yellow>", ChatColor.YELLOW + "");
        result = result.replaceAll("<white>", ChatColor.WHITE + "");
        
        // Format tags
        result = result.replaceAll("<bold>", ChatColor.BOLD + "");
        result = result.replaceAll("<italic>", ChatColor.ITALIC + "");
        result = result.replaceAll("<underlined>", ChatColor.UNDERLINE + "");
        result = result.replaceAll("<strikethrough>", ChatColor.STRIKETHROUGH + "");
        result = result.replaceAll("<obfuscated>", ChatColor.MAGIC + "");
        result = result.replaceAll("<reset>", ChatColor.RESET + "");
        
        // Closing tags
        result = result.replaceAll("</[^>]+>", ChatColor.RESET + "");
        
        // Hex colors (remove, BungeeCord doesn't support)
        result = result.replaceAll("<#[0-9a-fA-F]{6}>", "");
        
        // Gradients (remove, BungeeCord doesn't support)
        result = result.replaceAll("<gradient:[^>]+>", "");
        
        // Translate & codes
        result = ChatColor.translateAlternateColorCodes('&', result);
        
        return result;
    }
}