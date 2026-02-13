package me.d4vide106.maintenance.manager;

import me.d4vide106.maintenance.api.WhitelistedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistManager {
    
    private final Map<UUID, WhitelistedPlayer> cache = new ConcurrentHashMap<>();
    
    public WhitelistManager() {
    }
    
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.completedFuture(null);
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
        WhitelistedPlayer player = new WhitelistedPlayer(
            uuid,
            name,
            reason,
            System.currentTimeMillis(),
            null
        );
        cache.put(uuid, player);
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Void> remove(@NotNull UUID uuid) {
        cache.remove(uuid);
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Void> clearWhitelist() {
        cache.clear();
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Void> refresh() {
        return CompletableFuture.completedFuture(null);
    }
}
