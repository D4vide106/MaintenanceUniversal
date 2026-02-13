package me.d4vide106.maintenance.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Fired when a player is about to be kicked due to maintenance.
 * <p>
 * This event is cancellable. If cancelled, the player will not be kicked.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerMaintenanceKickEvent extends MaintenanceEvent implements Cancellable {
    
    private final UUID playerUuid;
    private final String playerName;
    private String kickMessage;
    private boolean cancelled;
    
    /**
     * Creates a new player kick event.
     * 
     * @param playerUuid the player UUID
     * @param playerName the player name
     * @param kickMessage the kick message
     */
    public PlayerMaintenanceKickEvent(
        @NotNull UUID playerUuid,
        @NotNull String playerName,
        @NotNull String kickMessage
    ) {
        super(false);
        this.playerUuid = Objects.requireNonNull(playerUuid);
        this.playerName = Objects.requireNonNull(playerName);
        this.kickMessage = Objects.requireNonNull(kickMessage);
        this.cancelled = false;
    }
    
    /**
     * Gets the player UUID.
     */
    @NotNull
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    /**
     * Gets the player name.
     */
    @NotNull
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Gets the kick message.
     */
    @NotNull
    public String getKickMessage() {
        return kickMessage;
    }
    
    /**
     * Sets a custom kick message.
     * 
     * @param kickMessage the new message (supports MiniMessage)
     */
    public void setKickMessage(@NotNull String kickMessage) {
        this.kickMessage = Objects.requireNonNull(kickMessage);
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