package me.d4vide106.maintenance.database;

import me.d4vide106.maintenance.config.MaintenanceConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Factory for creating database providers.
 */
public class DatabaseFactory {
    
    /**
     * Creates a database provider based on configuration.
     */
    @NotNull
    public static DatabaseProvider create(@NotNull MaintenanceConfig config, @NotNull Path dataFolder) {
        String type = config.getDatabaseType().toLowerCase();
        String tablePrefix = config.getDatabaseTablePrefix();
        
        switch (type) {
            case "sqlite":
                return new SQLiteDatabase(dataFolder.toFile(), tablePrefix);
            
            case "mysql":
                return new MySQLDatabase(
                    config.getDatabaseHost(),
                    config.getDatabasePort(),
                    config.getDatabaseName(),
                    config.getDatabaseUsername(),
                    config.getDatabasePassword(),
                    false, // useSSL - can be added to config later
                    config.getDatabasePoolSize(),
                    tablePrefix
                );
            
            case "postgresql":
            case "postgres":
                throw new UnsupportedOperationException("PostgreSQL support coming in v1.1.0");
            
            default:
                throw new IllegalArgumentException("Unknown database type: " + type);
        }
    }
}