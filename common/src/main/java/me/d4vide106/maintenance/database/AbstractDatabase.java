package me.d4vide106.maintenance.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    
    protected abstract HikariConfig getHikariConfig();
    protected abstract String[] getCreateTableStatements();
    
    @Override
    public @NotNull CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                HikariConfig config = getHikariConfig();
                dataSource = new HikariDataSource(config);
                
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
    
    // Maintenance Status
    
    @Override
    public @NotNull CompletableFuture<Boolean> isMaintenanceEnabled() {
        return supplyAsync(conn -> {
            String sql = "SELECT value FROM " + table("settings") + " WHERE key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "enabled");
                ResultSet rs = stmt.executeQuery();
                return rs.next() && "true".equals(rs.getString("value"));
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setMaintenanceEnabled(boolean enabled) {
        return runAsync(conn -> {
            String sql = "REPLACE INTO " + table("settings") + " (key, value) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "enabled");
                stmt.setString(2, String.valueOf(enabled));
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<String> getMaintenanceMode() {
        return supplyAsync(conn -> {
            String sql = "SELECT value FROM " + table("settings") + " WHERE key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "mode");
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("value") : "DISABLED";
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setMaintenanceMode(@NotNull String mode) {
        return runAsync(conn -> {
            String sql = "REPLACE INTO " + table("settings") + " (key, value) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "mode");
                stmt.setString(2, mode);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<String> getMaintenanceReason() {
        return supplyAsync(conn -> {
            String sql = "SELECT value FROM " + table("settings") + " WHERE key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "reason");
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("value") : null;
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setMaintenanceReason(@Nullable String reason) {
        return runAsync(conn -> {
            String sql = "REPLACE INTO " + table("settings") + " (key, value) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "reason");
                stmt.setString(2, reason);
                stmt.executeUpdate();
            }
        });
    }
    
    // Whitelist
    
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
            String sql = "REPLACE INTO " + table("whitelist") + 
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
    
    // Statistics
    
    @Override
    public @NotNull CompletableFuture<MaintenanceStats> getStats() {
        return supplyAsync(conn -> {
            String sql = "SELECT * FROM " + table("stats") + " WHERE id = 1";
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
    
    @Override
    public @NotNull CompletableFuture<Void> incrementSessions() {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET total_sessions = total_sessions + 1 WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> addDuration(long milliseconds) {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET total_duration = total_duration + ? WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, milliseconds);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> incrementPlayersKicked(int count) {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET players_kicked = players_kicked + ? WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, count);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> incrementConnectionsBlocked() {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET connections_blocked = connections_blocked + 1 WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setLastStarted(long timestamp) {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET last_started = ? WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, timestamp);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setLastEnded(long timestamp) {
        return runAsync(conn -> {
            String sql = "UPDATE " + table("stats") + " SET last_ended = ? WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, timestamp);
                stmt.executeUpdate();
            }
        });
    }
    
    // History
    
    @Override
    public @NotNull CompletableFuture<Void> saveSession(
        long startTime,
        long endTime,
        @NotNull String mode,
        @Nullable String reason,
        @Nullable String startedBy,
        int playersKicked
    ) {
        return runAsync(conn -> {
            String sql = "INSERT INTO " + table("history") + 
                        " (start_time, end_time, mode, reason, started_by, players_kicked) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, startTime);
                stmt.setLong(2, endTime);
                stmt.setString(3, mode);
                stmt.setString(4, reason);
                stmt.setString(5, startedBy);
                stmt.setInt(6, playersKicked);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<List<MaintenanceSession>> getRecentSessions(int limit) {
        return supplyAsync(conn -> {
            String sql = "SELECT * FROM " + table("history") + " ORDER BY start_time DESC LIMIT ?";
            List<MaintenanceSession> sessions = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    sessions.add(new MaintenanceSession(
                        rs.getInt("id"),
                        rs.getLong("start_time"),
                        rs.getLong("end_time"),
                        rs.getString("mode"),
                        rs.getString("reason"),
                        rs.getString("started_by"),
                        rs.getInt("players_kicked")
                    ));
                }
            }
            return sessions;
        });
    }
    
    // Scheduled Maintenance
    
    @Override
    public @NotNull CompletableFuture<Long> getScheduledStart() {
        return supplyAsync(conn -> {
            String sql = "SELECT scheduled_start FROM " + table("schedule") + " ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getLong("scheduled_start") : 0L;
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setScheduledStart(long timestamp) {
        return runAsync(conn -> {
            String sql = "REPLACE INTO " + table("schedule") + " (id, scheduled_start, scheduled_end) VALUES (1, ?, (SELECT scheduled_end FROM " + table("schedule") + " WHERE id = 1))";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, timestamp);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Long> getScheduledEnd() {
        return supplyAsync(conn -> {
            String sql = "SELECT scheduled_end FROM " + table("schedule") + " ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getLong("scheduled_end") : 0L;
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> setScheduledEnd(long timestamp) {
        return runAsync(conn -> {
            String sql = "REPLACE INTO " + table("schedule") + " (id, scheduled_start, scheduled_end) VALUES (1, (SELECT scheduled_start FROM " + table("schedule") + " WHERE id = 1), ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, timestamp);
                stmt.executeUpdate();
            }
        });
    }
    
    @Override
    public @NotNull CompletableFuture<Void> clearSchedule() {
        return runAsync(conn -> {
            String sql = "DELETE FROM " + table("schedule");
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        });
    }
}