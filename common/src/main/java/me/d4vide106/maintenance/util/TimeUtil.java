package me.d4vide106.maintenance.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for time formatting and conversion.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TimeUtil {
    
    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Formats a duration into a human-readable string.
     * <p>
     * Examples:
     * <ul>
     * <li>61 seconds → "1 minute, 1 second"</li>
     * <li>3665 seconds → "1 hour, 1 minute, 5 seconds"</li>
     * <li>90000 seconds → "1 day, 1 hour"</li>
     * </ul>
     * </p>
     * 
     * @param duration the duration to format
     * @return formatted string
     */
    @NotNull
    public static String format(@NotNull Duration duration) {
        long seconds = duration.getSeconds();
        
        if (seconds == 0) {
            return "0 seconds";
        }
        
        long days = TimeUnit.SECONDS.toDays(seconds);
        seconds -= TimeUnit.DAYS.toSeconds(days);
        
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        
        StringBuilder builder = new StringBuilder();
        
        if (days > 0) {
            builder.append(days).append(" day").append(days != 1 ? "s" : "");
        }
        
        if (hours > 0) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(hours).append(" hour").append(hours != 1 ? "s" : "");
        }
        
        if (minutes > 0) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(minutes).append(" minute").append(minutes != 1 ? "s" : "");
        }
        
        if (seconds > 0) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(seconds).append(" second").append(seconds != 1 ? "s" : "");
        }
        
        return builder.toString();
    }
    
    /**
     * Formats a duration into a compact string.
     * <p>
     * Examples:
     * <ul>
     * <li>61 seconds → "1m 1s"</li>
     * <li>3665 seconds → "1h 1m 5s"</li>
     * <li>90000 seconds → "1d 1h"</li>
     * </ul>
     * </p>
     */
    @NotNull
    public static String formatCompact(@NotNull Duration duration) {
        long seconds = duration.getSeconds();
        
        if (seconds == 0) {
            return "0s";
        }
        
        long days = TimeUnit.SECONDS.toDays(seconds);
        seconds -= TimeUnit.DAYS.toSeconds(days);
        
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        
        StringBuilder builder = new StringBuilder();
        
        if (days > 0) builder.append(days).append("d ");
        if (hours > 0) builder.append(hours).append("h ");
        if (minutes > 0) builder.append(minutes).append("m ");
        if (seconds > 0) builder.append(seconds).append("s");
        
        return builder.toString().trim();
    }
    
    /**
     * Parses a time string into a Duration.
     * <p>
     * Supported formats:
     * <ul>
     * <li>"5m" → 5 minutes</li>
     * <li>"2h" → 2 hours</li>
     * <li>"1d" → 1 day</li>
     * <li>"1h30m" → 1 hour 30 minutes</li>
     * <li>"300" → 300 seconds</li>
     * </ul>
     * </p>
     */
    @NotNull
    public static Duration parse(@NotNull String input) throws IllegalArgumentException {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be empty");
        }
        
        // Try parsing as plain number (seconds)
        try {
            return Duration.ofSeconds(Long.parseLong(input));
        } catch (NumberFormatException ignored) {
            // Continue to pattern parsing
        }
        
        long totalSeconds = 0;
        StringBuilder number = new StringBuilder();
        
        for (char c : input.toLowerCase().toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else if (Character.isLetter(c)) {
                if (number.length() == 0) {
                    throw new IllegalArgumentException("Invalid time format: " + input);
                }
                
                long value = Long.parseLong(number.toString());
                number.setLength(0);
                
                switch (c) {
                    case 'd':
                        totalSeconds += TimeUnit.DAYS.toSeconds(value);
                        break;
                    case 'h':
                        totalSeconds += TimeUnit.HOURS.toSeconds(value);
                        break;
                    case 'm':
                        totalSeconds += TimeUnit.MINUTES.toSeconds(value);
                        break;
                    case 's':
                        totalSeconds += value;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown time unit: " + c);
                }
            } else if (!Character.isWhitespace(c)) {
                throw new IllegalArgumentException("Invalid character in time format: " + c);
            }
        }
        
        if (number.length() > 0) {
            throw new IllegalArgumentException("Time value without unit: " + number);
        }
        
        if (totalSeconds == 0) {
            throw new IllegalArgumentException("Time cannot be zero");
        }
        
        return Duration.ofSeconds(totalSeconds);
    }
}