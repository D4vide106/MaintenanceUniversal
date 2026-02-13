package me.d4vide106.maintenance.database;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * SQLite database implementation.
 * <p>
 * Recommended for single servers without shared database needs.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class SQLiteDatabase extends AbstractDatabase {
    
    private final File databaseFile;
    
    public SQLiteDatabase(@NotNull File dataFolder, @NotNull String tablePrefix) {
        super(tablePrefix);
        this.databaseFile = new File(dataFolder, "maintenance.db");
    }
    
    @Override
    protected HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("MaintenanceDB-SQLite");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(1); // SQLite doesn't support concurrent writes
        config.setMinimumIdle(1);
        config.setMaxLifetime(60000);
        config.setConnectionTimeout(5000);
        return config;
    }
    
    @Override
    protected String[] getCreateTableStatements() {
        return new String[] {
            // Settings table
            "CREATE TABLE IF NOT EXISTS " + table("settings") + " (" +
            "  key TEXT PRIMARY KEY," +
            "  value TEXT NOT NULL" +
            ")",
            
            // Whitelist table
            "CREATE TABLE IF NOT EXISTS " + table("whitelist") + " (" +
            "  uuid TEXT PRIMARY KEY," +
            "  name TEXT NOT NULL," +
            "  reason TEXT," +
            "  added_at INTEGER NOT NULL," +
            "  added_by TEXT" +
            ")",
            
            // Statistics table
            "CREATE TABLE IF NOT EXISTS " + table("stats") + " (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  total_sessions INTEGER DEFAULT 0," +
            "  total_duration INTEGER DEFAULT 0," +
            "  last_started INTEGER DEFAULT 0," +
            "  last_ended INTEGER DEFAULT 0," +
            "  current_whitelisted INTEGER DEFAULT 0," +
            "  players_kicked INTEGER DEFAULT 0," +
            "  connections_blocked INTEGER DEFAULT 0" +
            ")",
            
            // History table
            "CREATE TABLE IF NOT EXISTS " + table("history") + " (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  start_time INTEGER NOT NULL," +
            "  end_time INTEGER NOT NULL," +
            "  mode TEXT NOT NULL," +
            "  reason TEXT," +
            "  started_by TEXT," +
            "  players_kicked INTEGER DEFAULT 0" +
            ")",
            
            // Schedule table
            "CREATE TABLE IF NOT EXISTS " + table("schedule") + " (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  scheduled_start INTEGER NOT NULL," +
            "  scheduled_end INTEGER NOT NULL" +
            ")",
            
            // Initialize default stats row
            "INSERT OR IGNORE INTO " + table("stats") + " (id) VALUES (1)"
        };
    }
}