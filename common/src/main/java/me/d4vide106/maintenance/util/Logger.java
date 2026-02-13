package me.d4vide106.maintenance.util;

import org.jetbrains.annotations.NotNull;

/**
 * Simple logger interface for cross-platform logging.
 * <p>
 * Each platform implementation should provide its own logger adapter.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Logger {
    
    /**
     * Logs an info message.
     */
    void info(@NotNull String message);
    
    /**
     * Logs a warning message.
     */
    void warn(@NotNull String message);
    
    /**
     * Logs an error message.
     */
    void error(@NotNull String message);
    
    /**
     * Logs an error with exception.
     */
    void error(@NotNull String message, @NotNull Throwable throwable);
    
    /**
     * Logs a debug message (only if debug is enabled).
     */
    void debug(@NotNull String message);
}