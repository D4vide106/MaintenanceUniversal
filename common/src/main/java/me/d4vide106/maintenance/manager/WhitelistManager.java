package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.redis.RedisManager;
import me.d4vide106.maintenance.redis.RedisMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for maintenance whitelist operations.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class WhitelistManager {
    
    private final DatabaseProvider database;
    private final RedisManager redis;
    private final String serverName;
    
    // Cache for quick lookup
    private final Map<UUID, WhitelistedPlayer> cache = new ConcurrentHashMap<>();
    
    public WhitelistManager(
        @NotNull DatabaseProvider database,
        @Nullable RedisManager redis,
        @NotNull String serverName
    ) {
        this.database = database;
        this.redis = redis;
        this.serverName = serverName;
    }
    
    /**
     * Initializes the manager and loads whitelist from database.
     */
    public CompletableFuture<Void> initialize() {
        return database.getWhitelistedPlayers().thenAccept(players -> {
            cache.clear();
            players.forEach(player -> cache.put(player.getUuid(), player));
        });
    }
    
    /**
     * Checks if a player is whitelisted.
     */
    public boolean isWhitelisted(@NotNull UUID uuid) {
        return cache.containsKey(uuid);
    }
    
    /**
     * Gets all whitelisted players.
     */
    @NotNull
    public List<WhitelistedPlayer> getWhitelistedPlayers() {
        return List.copyOf(cache.values());
    }
    
    /**
     * Gets whitelist count.
     */
    public int getWhitelistCount() {
        return cache.size();
    }
    
    /**
     * Adds a player to the whitelist.
     */
    public CompletableFuture<Boolean> addToWhitelist(
        @NotNull UUID uuid,
        @NotNull String name,
        @Nullable String reason,
        @Nullable String addedBy
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (cache.containsKey(uuid)) {
                return false; // Already whitelisted
            }
            
            WhitelistedPlayer player = new WhitelistedPlayer(
                uuid,
                name,
                reason,
                System.currentTimeMillis(),
                addedBy
            );
            
            // Add to cache
            cache.put(uuid, player);
            
            // Save to database
            database.addToWhitelist(player).join();
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.WHITELIST_ADDED,
                    serverName
                )
                .set("uuid", uuid.toString())
                .set("name", name)
                .set("reason", reason);
                redis.publish(msg).join();
            }
            
            return true;
        });
    }
    
    /**
     * Removes a player from the whitelist.
     */
    public CompletableFuture<Boolean> removeFromWhitelist(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (!cache.containsKey(uuid)) {
                return false; // Not whitelisted
            }
            
            // Remove from cache
            cache.remove(uuid);
            
            // Remove from database
            database.removeFromWhitelist(uuid).join();
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.WHITELIST_REMOVED,
                    serverName
                )
                .set("uuid", uuid.toString());
                redis.publish(msg).join();
            }
            
            return true;
        });
    }
    
    /**
     * Clears the entire whitelist.
     */
    public CompletableFuture<Void> clearWhitelist() {
        return CompletableFuture.runAsync(() -> {
            // Clear cache
            cache.clear();
            
            // Clear database
            database.clearWhitelist().join();
            
            // Broadcast via Redis
            if (redis != null && redis.isConnected()) {
                RedisMessage msg = new RedisMessage(
                    RedisMessage.MessageType.WHITELIST_CLEARED,
                    serverName
                );
                redis.publish(msg).join();
            }
        });
    }
    
    /**
     * Refreshes the cache from database.
     */
    public CompletableFuture<Void> refresh() {
        return initialize();
    }
}