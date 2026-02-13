package me.d4vide106.maintenance.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ValidationUtil {
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    );
    
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Validates a Minecraft username.
     * <p>
     * Valid usernames:
     * <ul>
     * <li>3-16 characters</li>
     * <li>Only alphanumeric and underscores</li>
     * </ul>
     * </p>
     */
    public static boolean isValidUsername(@NotNull String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validates a UUID string.
     */
    public static boolean isValidUUID(@NotNull String uuid) {
        return UUID_PATTERN.matcher(uuid.toLowerCase()).matches();
    }
    
    /**
     * Parses a UUID string safely.
     * 
     * @return UUID or null if invalid
     */
    public static UUID parseUUID(@NotNull String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Validates a server name.
     * <p>
     * Server names should:
     * <ul>
     * <li>Not be empty</li>
     * <li>Not contain special characters except hyphens and underscores</li>
     * <li>Be between 1-32 characters</li>
     * </ul>
     * </p>
     */
    public static boolean isValidServerName(@NotNull String name) {
        return !name.isEmpty() 
            && name.length() <= 32 
            && Pattern.matches("^[a-zA-Z0-9_-]+$", name);
    }
    
    /**
     * Sanitizes input to prevent SQL injection.
     */
    @NotNull
    public static String sanitize(@NotNull String input) {
        return input.replace("'", "''").replace("\\", "\\\\");
    }
}