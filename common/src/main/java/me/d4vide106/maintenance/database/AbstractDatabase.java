package me.d4vide106.maintenance.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract base class for database implementations.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractDatabase implements DatabaseProvider {
    
    protected HikariDataSource dataSource;
    protected final ExecutorService executor;
    protected final String tablePrefix;
    
    public AbstractDatabase(@NotNull String tablePrefix) {
        this.tablePrefix = tablePrefix;
        this.executor = Executors.newFixedThreadPool(
            4,
            r -> {
                Thread thread = new Thread(r);
                thread.setName("MaintenanceDB-" + thread.getId());
                thread.setDaemon(true);
                return thread;
            }
        );
    }
    
    /**
     * Gets HikariCP configuration for this database type.
     */
    protected abstract HikariConfig getHikariConfig();
    
    /**
     * Gets SQL for creating tables (database-specific).
     */
    protected abstract String[] getCreateTableStatements();
    
    @Override
    public @NotNull CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                HikariConfig config = getHikariConfig();
                dataSource = new HikariDataSource(config);
                
                // Create tables
                try (Connection conn = dataSource.getConnection()) {
                    for (String sql : getCreateTableStatements()) {
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.execute();
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize database", e);
            }
        }, executor);
    }
    
    @Override
    public @NotNull CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
            executor.shutdown();
        });
    }
    
    @Override
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }
    
    // ============================================
    // HELPER METHODS
    // ============================================
    
    protected String table(String name) {
        return tablePrefix + name;
    }
    
    protected <T> CompletableFuture<T> supplyAsync(DatabaseTask<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                return task.execute(conn);
            } catch (SQLException e) {
                throw new RuntimeException("Database operation failed", e);
            }
        }, executor);
    }
    
    protected CompletableFuture<Void> runAsync(DatabaseRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                runnable.run(conn);
            } catch (SQLException e) {
                throw new RuntimeException("Database operation failed", e);
            }
        }, executor);
    }
    
    @FunctionalInterface
    protected interface DatabaseTask<T> {
        T execute(Connection conn) throws SQLException;
    }
    
    @FunctionalInterface
    protected interface DatabaseRunnable {
        void run(Connection conn) throws SQLException;
    }
    
    // ============================================
    // DEFAULT IMPLEMENTATIONS
    // ============================================
    
    @Override
    public @NotNull CompletableFuture<Boolean> isMaintenanceEnabled() {
        return supplyAsync(conn -> {
            String sql = "SELECT value FROM " + table("settings") + " WHERE key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "enabled");
                ResultSet rs = stmt.executeQuery();
                return rs.next() && rs.getBoolean("value");
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setMaintenanceEnabled(boolean enabled) {
        return runAsync(conn -> {
            String sql = "INSERT OR REPLACE INTO " + table("settings") + " (key, value) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "enabled");
                stmt.setBoolean(2, enabled);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Boolean> isWhitelisted(@NotNull UUID uuid) {
        return supplyAsync(conn -> {
            String sql = "SELECT COUNT(*) FROM " + table("whitelist") + " WHERE uuid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<List<WhitelistedPlayer>> getWhitelistedPlayers() {
        return supplyAsync(conn -> {
            String sql = "SELECT * FROM " + table("whitelist");
            List<WhitelistedPlayer> players = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    players.add(new WhitelistedPlayer(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getString("reason"),
                        rs.getLong("added_at"),
                        rs.getString("added_by")
                    ));
                }
            }
            return players;
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> addToWhitelist(@NotNull WhitelistedPlayer player) {
        return runAsync(conn -> {
            String sql = "INSERT OR REPLACE INTO " + table("whitelist") + 
                        " (uuid, name, reason, added_at, added_by) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, player.getUuid().toString());
                stmt.setString(2, player.getName());
                stmt.setString(3, player.getReason());
                stmt.setLong(4, player.getAddedAt());
                stmt.setString(5, player.getAddedBy());
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> removeFromWhitelist(@NotNull UUID uuid) {
        return runAsync(conn -> {
            String sql = "DELETE FROM " + table("whitelist") + " WHERE uuid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> clearWhitelist() {
        return runAsync(conn -> {
            String sql = "DELETE FROM " + table("whitelist");
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<MaintenanceStats> getStats() {
        return supplyAsync(conn -> {
            String sql = "SELECT * FROM " + table("stats");
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new MaintenanceStats(
                        rs.getInt("total_sessions"),
                        Duration.ofMillis(rs.getLong("total_duration")),
                        rs.getLong("last_started"),
                        rs.getLong("last_ended"),
                        rs.getInt("current_whitelisted"),
                        rs.getInt("players_kicked"),
                        rs.getInt("connections_blocked")
                    );
                }
                return new MaintenanceStats(0, Duration.ZERO, 0, 0, 0, 0, 0);
            }
        });
    }
}