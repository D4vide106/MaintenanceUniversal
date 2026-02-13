package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Fired when a player is removed from the maintenance whitelist.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceWhitelistRemoveEvent extends MaintenanceEvent implements Cancellable {
    
    private final UUID playerUuid;
    private final String playerName;
    private final String removedBy;
    private boolean cancelled;
    
    public MaintenanceWhitelistRemoveEvent(
        @NotNull UUID playerUuid,
        @NotNull String playerName,
        @Nullable String removedBy
    ) {
        super(false);
        this.playerUuid = Objects.requireNonNull(playerUuid);
        this.playerName = Objects.requireNonNull(playerName);
        this.removedBy = removedBy;
        this.cancelled = false;
    }
    
    @NotNull
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    @NotNull
    public String getPlayerName() {
        return playerName;
    }
    
    @Nullable
    public String getRemovedBy() {
        return removedBy;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}