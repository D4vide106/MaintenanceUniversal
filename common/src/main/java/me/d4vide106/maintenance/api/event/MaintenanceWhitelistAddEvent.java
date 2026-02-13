package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Fired when a player is added to the maintenance whitelist.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaintenanceWhitelistAddEvent extends MaintenanceEvent implements Cancellable {
    
    private final UUID playerUuid;
    private final String playerName;
    private final String reason;
    private final String addedBy;
    private boolean cancelled;
    
    public MaintenanceWhitelistAddEvent(
        @NotNull UUID playerUuid,
        @NotNull String playerName,
        @Nullable String reason,
        @Nullable String addedBy
    ) {
        super(false);
        this.playerUuid = Objects.requireNonNull(playerUuid);
        this.playerName = Objects.requireNonNull(playerName);
        this.reason = reason;
        this.addedBy = addedBy;
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
    public String getReason() {
        return reason;
    }
    
    @Nullable
    public String getAddedBy() {
        return addedBy;
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