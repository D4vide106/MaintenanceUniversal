package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Fired when a player attempts to join during maintenance.
 * <p>
 * This event is cancellable. If cancelled, the player will be allowed to join.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerMaintenanceJoinEvent extends MaintenanceEvent implements Cancellable {
    
    private final UUID playerUuid;
    private final String playerName;
    private final String ipAddress;
    private final boolean whitelisted;
    private String denyMessage;
    private boolean cancelled;
    
    /**
     * Creates a new maintenance join event.
     * 
     * @param playerUuid the player UUID
     * @param playerName the player name
     * @param ipAddress the player IP address
     * @param whitelisted whether the player is whitelisted
     * @param denyMessage the deny message if not whitelisted
     */
    public PlayerMaintenanceJoinEvent(
        @NotNull UUID playerUuid,
        @NotNull String playerName,
        @NotNull String ipAddress,
        boolean whitelisted,
        @Nullable String denyMessage
    ) {
        super(false);
        this.playerUuid = Objects.requireNonNull(playerUuid);
        this.playerName = Objects.requireNonNull(playerName);
        this.ipAddress = Objects.requireNonNull(ipAddress);
        this.whitelisted = whitelisted;
        this.denyMessage = denyMessage;
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
    
    @NotNull
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Checks if the player is whitelisted.
     */
    public boolean isWhitelisted() {
        return whitelisted;
    }
    
    /**
     * Gets the deny message.
     */
    @Nullable
    public String getDenyMessage() {
        return denyMessage;
    }
    
    /**
     * Sets a custom deny message.
     */
    public void setDenyMessage(@NotNull String denyMessage) {
        this.denyMessage = Objects.requireNonNull(denyMessage);
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