package me.d4vide106.maintenance.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a whitelisted player in maintenance mode.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class WhitelistedPlayer {
    
    private final UUID uuid;
    private final String name;
    private final String reason;
    private final long addedAt;
    private final String addedBy;
    
    /**
     * Creates a new WhitelistedPlayer instance.
     * 
     * @param uuid the player UUID
     * @param name the player name
     * @param reason the whitelist reason (can be null)
     * @param addedAt the timestamp when added
     * @param addedBy who added this player (can be null)
     */
    public WhitelistedPlayer(
        @NotNull UUID uuid,
        @NotNull String name,
        @Nullable String reason,
        long addedAt,
        @Nullable String addedBy
    ) {
        this.uuid = Objects.requireNonNull(uuid, "UUID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.reason = reason;
        this.addedAt = addedAt;
        this.addedBy = addedBy;
    }
    
    /**
     * Gets the player UUID.
     * 
     * @return the UUID
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }
    
    /**
     * Gets the player name.
     * 
     * @return the name
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Gets the whitelist reason.
     * 
     * @return the reason, or null if not specified
     */
    @Nullable
    public String getReason() {
        return reason;
    }
    
    /**
     * Gets when this player was added to the whitelist.
     * 
     * @return Unix timestamp in milliseconds
     */
    public long getAddedAt() {
        return addedAt;
    }
    
    /**
     * Gets who added this player to the whitelist.
     * 
     * @return the admin name/UUID, or null if not tracked
     */
    @Nullable
    public String getAddedBy() {
        return addedBy;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhitelistedPlayer)) return false;
        WhitelistedPlayer that = (WhitelistedPlayer) o;
        return uuid.equals(that.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    @Override
    public String toString() {
        return "WhitelistedPlayer{" +
               "uuid=" + uuid +
               ", name='" + name + '\'' +
               ", reason='" + reason + '\'' +
               ", addedAt=" + addedAt +
               ", addedBy='" + addedBy + '\'' +
               '}';
    }
}