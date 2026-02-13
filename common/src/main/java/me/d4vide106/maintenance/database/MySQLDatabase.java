package me.d4vide106.maintenance.database;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;

/**
 * MySQL/MariaDB database implementation.
 * <p>
 * Recommended for multi-server networks with shared database.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MySQLDatabase extends AbstractDatabase {
    
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean useSSL;
    private final int poolSize;
    
    public MySQLDatabase(
        @NotNull String host,
        int port,
        @NotNull String database,
        @NotNull String username,
        @NotNull String password,
        boolean useSSL,
        int poolSize,
        @NotNull String tablePrefix
    ) {
        super(tablePrefix);
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.useSSL = useSSL;
        this.poolSize = poolSize;
    }
    
    @Override
    protected HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MaintenanceDB-MySQL");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=%s&autoReconnect=true",
            host, port, database, useSSL
        ));
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(5000);
        config.setConnectionTestQuery("SELECT 1");
        
        // Performance settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        return config;
    }
    
    @Override
    protected String[] getCreateTableStatements() {
        return new String[] {
            // Settings table
            "CREATE TABLE IF NOT EXISTS " + table("settings") + " (" +
            "  `key` VARCHAR(255) PRIMARY KEY," +
            "  `value` TEXT NOT NULL" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
            
            // Whitelist table
            "CREATE TABLE IF NOT EXISTS " + table("whitelist") + " (" +
            "  uuid VARCHAR(36) PRIMARY KEY," +
            "  name VARCHAR(16) NOT NULL," +
            "  reason TEXT," +
            "  added_at BIGINT NOT NULL," +
            "  added_by VARCHAR(255)," +
            "  INDEX idx_name (name)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
            
            // Statistics table
            "CREATE TABLE IF NOT EXISTS " + table("stats") + " (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT," +
            "  total_sessions INT DEFAULT 0," +
            "  total_duration BIGINT DEFAULT 0," +
            "  last_started BIGINT DEFAULT 0," +
            "  last_ended BIGINT DEFAULT 0," +
            "  current_whitelisted INT DEFAULT 0," +
            "  players_kicked INT DEFAULT 0," +
            "  connections_blocked INT DEFAULT 0" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
            
            // History table
            "CREATE TABLE IF NOT EXISTS " + table("history") + " (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT," +
            "  start_time BIGINT NOT NULL," +
            "  end_time BIGINT NOT NULL," +
            "  mode VARCHAR(50) NOT NULL," +
            "  reason TEXT," +
            "  started_by VARCHAR(255)," +
            "  players_kicked INT DEFAULT 0," +
            "  INDEX idx_start_time (start_time)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
            
            // Schedule table
            "CREATE TABLE IF NOT EXISTS " + table("schedule") + " (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT," +
            "  scheduled_start BIGINT NOT NULL," +
            "  scheduled_end BIGINT NOT NULL" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
            
            // Initialize default stats row
            "INSERT IGNORE INTO " + table("stats") + " (id) VALUES (1)"
        };
    }
}