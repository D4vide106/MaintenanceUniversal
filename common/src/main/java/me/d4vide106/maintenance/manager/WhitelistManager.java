package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.WhitelistedPlayer;
import me.d4vide106.maintenance.database.DatabaseProvider;
import me.d4vide106.maintenance.redis.RedisManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistManager {
    
    private final DatabaseProvider database;
    private final RedisManager redis;
    private final String serverName;
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
    
    public CompletableFuture<Void> initialize() {
        return database.getWhitelistedPlayers().thenAccept(players -> {
            cache.clear();
            players.forEach(p -> cache.put(p.getUuid(), p));
        });
    }
    
    public boolean isWhitelisted(@NotNull UUID uuid) {
        return cache.containsKey(uuid);
    }
    
    public List<WhitelistedPlayer> getWhitelistedPlayers() {
        return new ArrayList<>(cache.values());
    }
    
    public CompletableFuture<Void> add(
        @NotNull UUID uuid,
        @NotNull String name,
        @Nullable String reason
    ) {
        WhitelistedPlayer player = new WhitelistedPlayer(uuid, name, reason, System.currentTimeMillis());
        cache.put(uuid, player);
        return database.addWhitelistedPlayer(player);
    }
    
    public CompletableFuture<Void> remove(@NotNull UUID uuid) {
        cache.remove(uuid);
        return database.removeWhitelistedPlayer(uuid);
    }
    
    public CompletableFuture<Void> clearWhitelist() {
        cache.clear();
        return database.clearWhitelist();
    }
    
    public CompletableFuture<Void> refresh() {
        return initialize();
    }
}
