package me.d4vide106.maintenance.database;

import me.d4vide106.maintenance.config.MaintenanceConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory for creating database providers based on configuration.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseFactory {
    
    /**
     * Creates a database provider based on configuration.
     * 
     * @param config the maintenance configuration
     * @param dataFolder the plugin data folder
     * @return configured database provider
     * @throws IllegalArgumentException if database type is unsupported
     */
    @NotNull
    public static DatabaseProvider create(
        @NotNull MaintenanceConfig config,
        @NotNull File dataFolder
    ) {
        String type = config.getDatabaseType().toUpperCase();
        String tablePrefix = config.table("table-prefix");
        
        switch (type) {
            case "SQLITE":
                return new SQLiteDatabase(dataFolder, tablePrefix);
            
            case "MYSQL":
            case "MARIADB":
                return new MySQLDatabase(
                    config.getDatabaseHost(),
                    config.getDatabasePort(),
                    config.getDatabaseName(),
                    config.getDatabaseUsername(),
                    config.getDatabasePassword(),
                    false, // SSL from config
                    config.getDatabasePoolSize(),
                    tablePrefix
                );
            
            case "POSTGRESQL":
                // TODO: Implement PostgreSQL
                throw new UnsupportedOperationException("PostgreSQL support coming soon");
            
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
}