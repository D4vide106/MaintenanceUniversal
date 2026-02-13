package me.d4vide106.maintenance.database;

import me.d4vide106.maintenance.config.MaintenanceConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating database providers.
 */
public class DatabaseFactory {
    
    /**
     * Creates a database provider based on configuration.
     */
    @NotNull
    public static DatabaseProvider create(@NotNull MaintenanceConfig config) {
        String type = config.getDatabaseType().toLowerCase();
        String tablePrefix = config.getDatabaseTablePrefix();
        
        switch (type) {
            case "sqlite":
                return new SQLiteDatabase(tablePrefix);
            
            case "mysql":
                return new MySQLDatabase(
                    tablePrefix,
                    config.getDatabaseHost(),
                    config.getDatabasePort(),
                    config.getDatabaseName(),
                    config.getDatabaseUsername(),
                    config.getDatabasePassword(),
                    config.getDatabasePoolSize()
                );
            
            case "postgresql":
            case "postgres":
                // TODO: Implement PostgreSQL
                throw new UnsupportedOperationException("PostgreSQL support coming soon");
            
            default:
                throw new IllegalArgumentException("Unknown database type: " + type);
        }
    }
}