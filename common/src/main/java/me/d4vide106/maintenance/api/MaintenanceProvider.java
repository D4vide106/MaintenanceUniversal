package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Provider class for accessing the MaintenanceAPI instance.
 * <p>
 * This class uses a singleton pattern to provide global access to the API.
 * The instance is set by the platform-specific implementation during plugin initialization.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MaintenanceProvider {
    
    private static MaintenanceAPI instance;
    
    private MaintenanceProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    
    /**
     * Gets the MaintenanceAPI instance.
     * 
     * @return the API instance
     * @throws IllegalStateException if the API has not been initialized
     */
    @NotNull
    public static MaintenanceAPI get() {
        if (instance == null) {
            throw new IllegalStateException(
                "MaintenanceAPI has not been initialized yet. " +
                "This usually means the plugin hasn't loaded properly."
            );
        }
        return instance;
    }
    
    /**
     * Sets the API instance.
     * <p>
     * This method should only be called by the platform implementation during initialization.
     * </p>
     * 
     * @param api the API instance to set
     * @throws IllegalStateException if the API has already been initialized
     */
    @ApiStatus.Internal
    public static void register(@NotNull MaintenanceAPI api) {
        if (instance != null) {
            throw new IllegalStateException("MaintenanceAPI has already been initialized");
        }
        instance = api;
    }
    
    /**
     * Unregisters the API instance.
     * <p>
     * This method should only be called during plugin shutdown.
     * </p>
     */
    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }
    
    /**
     * Checks if the API has been initialized.
     * 
     * @return true if the API is available
     */
    public static boolean isInitialized() {
        return instance != null;
    }
}